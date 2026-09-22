//[Fully AI generated code by GTP-6 Astra Medium]
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Captures a photo locally in the browser; nothing is installed or saved. */
public class CameraSeed {
    private static final int PIXEL_BYTES = 320 * 240 * 4; // Canvas RGBA pixels
    private static final long TIMEOUT_SECONDS = 120;

    /** Returns a photo-derived seed, or throws without changing the generator. */
    public static long byCamera() {
        HttpServer server = null;
        CompletableFuture<Long> result = new CompletableFuture<>();
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            String origin = "http://127.0.0.1:" + server.getAddress().getPort();
            long deadline = System.currentTimeMillis() + TIMEOUT_SECONDS * 1000;
            server.createContext("/", exchange -> handle(exchange, origin, deadline, result));
            server.start();
            System.out.println("Open this page to take a photo (two-minute limit): " + origin + "/");
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI.create(origin + "/"));
                } else {
                    System.out.println("Open the URL above in your browser manually.");
                }
            } catch (IOException | RuntimeException e) {
                System.out.println("Could not open the browser. Open the URL above manually.");
            }
            return result.get(Math.max(1, deadline - System.currentTimeMillis()), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Timed out waiting for a photo. Please try again.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Camera capture interrupted.", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause().getMessage(), e.getCause());
        } catch (IOException e) {
            throw new IllegalStateException("Could not start the local camera page.", e);
        } finally {
            if (server != null) server.stop(0);
        }
    }

    private static void handle(HttpExchange exchange, String origin, long deadline,
            CompletableFuture<Long> result) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            if ("GET".equals(exchange.getRequestMethod()) && "/".equals(path)) {
                reply(exchange, 200, "text/html; charset=utf-8", PAGE.replace("__DEADLINE__", Long.toString(deadline)));
                return;
            }
            // Only our page can send these POST requests; do not enable CORS.
            if (!"POST".equals(exchange.getRequestMethod())
                    || !origin.equals(exchange.getRequestHeaders().getFirst("Origin"))
                    || !"1".equals(exchange.getRequestHeaders().getFirst("X-Camera-Seed"))) {
                reply(exchange, 403, "text/plain", "Request not allowed.");
                return;
            }
            if (result.isDone() || System.currentTimeMillis() >= deadline) {
                reply(exchange, 410, "text/plain", "Capture session ended.");
                return;
            }
            if ("/capture".equals(path)) {
                byte[] pixels = exchange.getRequestBody().readNBytes(PIXEL_BYTES + 1);
                if (pixels.length != PIXEL_BYTES) {
                    reply(exchange, 400, "text/plain", "Expected a 320 by 240 RGBA photo.");
                    return;
                }
                long seed = seedFromPixels(pixels);
                reply(exchange, 200, "text/plain", "Seed applied: " + seed + ". Return to the Java program.");
                result.complete(seed);
            } else if ("/cancel".equals(path) || "/error".equals(path)) {
                String code = new String(exchange.getRequestBody().readNBytes(100), StandardCharsets.UTF_8);
                String message = "/cancel".equals(path) ? "Capture cancelled." : switch (code) {
                    case "NotAllowedError" -> "Camera permission was denied. Allow camera access and try again.";
                    case "NotFoundError" -> "No camera was found.";
                    case "NotReadableError" -> "Camera is unavailable or in use by another application.";
                    default -> "Could not access the camera in this browser.";
                };
                reply(exchange, 200, "text/plain", message + " Return to the Java program.");
                result.completeExceptionally(new IllegalStateException(message));
            } else {
                reply(exchange, 404, "text/plain", "Not found.");
            }
        }
    }

    private static long seedFromPixels(byte[] pixels) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(pixels);
            // Bounded for the existing LCG's multiplication and modulus.
            return new BigInteger(1, hash).mod(BigInteger.valueOf(1_000_000_006L)).longValue() + 1;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable.", e);
        }
    }

    private static void reply(HttpExchange exchange, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    private static final String PAGE = """
            <!doctype html>
            <html lang="en"><head><meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>Camera seed</title>
            <style>
              body { font: 18px system-ui; max-width: 640px; margin: 40px auto; padding: 20px; }
              video { width: 100%; background: #222; border-radius: 12px; }
              button { font: inherit; padding: 12px; margin: 12px 8px 0 0; cursor: pointer; }
            </style></head><body>
            <h1>Take a photo for your seed</h1>
            <p>The photo stays on this computer and is not saved. Allow camera access, then capture a frame.</p>
            <video id="preview" autoplay muted playsinline></video>
            <button id="capture" disabled>Capture and use seed</button>
            <button id="cancel">Cancel</button>
            <p id="status" role="status">Waiting for camera permission…</p>
            <script>
              const video = document.getElementById('preview');
              const capture = document.getElementById('capture');
              const cancel = document.getElementById('cancel');
              const status = document.getElementById('status');
              let stream, finished = false;
              function stop() {
                finished = true;
                capture.disabled = cancel.disabled = true;
                if (stream) stream.getTracks().forEach(track => track.stop());
              }
              async function post(path, body) {
                const response = await fetch(path, {
                  method: 'POST', headers: {'X-Camera-Seed': '1'}, body
                });
                const message = await response.text();
                if (!response.ok) throw new Error(message);
                return message;
              }
              async function finish(path, body) {
                stop();
                try { status.textContent = await post(path, body); }
                catch (error) {
                  status.textContent = 'Capture could not finish: ' + error.message
                    + '. Return to the Java program; the session expires after two minutes.';
                }
              }
              capture.onclick = () => {
                if (finished) return;
                try {
                  const canvas = document.createElement('canvas');
                  canvas.width = 320; canvas.height = 240;
                  const context = canvas.getContext('2d');
                  context.drawImage(video, 0, 0, 320, 240);
                  const pixels = context.getImageData(0, 0, 320, 240).data;
                  finish('/capture', new Uint8Array(pixels.buffer));
                } catch (error) { finish('/error', error.name); }
              };
              cancel.onclick = () => { if (!finished) finish('/cancel', ''); };
              window.addEventListener('pagehide', () => {
                if (!finished) {
                  stop();
                  fetch('/cancel', {method: 'POST', headers: {'X-Camera-Seed': '1'},
                    body: '', keepalive: true}).catch(() => {});
                }
              });
              setTimeout(() => {
                if (!finished) {
                  stop();
                  status.textContent = 'Session timed out. Return to the Java program and choose option 7 again.';
                }
              }, Math.max(0, __DEADLINE__ - Date.now()));
              async function start() {
                try {
                  stream = await navigator.mediaDevices.getUserMedia({video: true, audio: false});
                  if (finished) { stream.getTracks().forEach(track => track.stop()); return; }
                  video.srcObject = stream;
                  await video.play();
                  if (finished) return;
                  capture.disabled = false;
                  status.textContent = 'Camera ready. Capture a photo when you are ready.';
                } catch (error) { if (!finished) await finish('/error', error.name); }
              }
              start();
            </script></body></html>
            """;
}
