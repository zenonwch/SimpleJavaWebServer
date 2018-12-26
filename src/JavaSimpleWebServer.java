import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class JavaSimpleWebServer {
    private static final String RESPONSE = String.format("<html>%n" +
            "<head>%n" +
            "<title>Java Simple Web Server</title>%n" +
            "</head>%n" +
            "<h1>Welcome to Simple Java Web Server!</h1>%n" +
            "</html>");

    private static final int fNumberOfThreads = 100;
    private static final Executor fThreadPool = Executors.newFixedThreadPool(fNumberOfThreads);

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(final String[] args) throws IOException {
        try (final ServerSocket socket = new ServerSocket(8080)) {
            while (true) {
                final Runnable task = () -> HandleRequest(socket);
                fThreadPool.execute(task);
            }
        }
    }

    private static void HandleRequest(final ServerSocket socket) {
        try (final Socket s = socket.accept();
             final InputStream is = s.getInputStream();
             final BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             final OutputStream outputStream = s.getOutputStream();
             final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             final PrintWriter out = new PrintWriter(writer, true)
        ) {
            final String webServerAddress = s.getInetAddress().toString();
            System.out.println("New Connection:" + webServerAddress);

            final String request = in.readLine();
            System.out.println("--- Client request: " + request);

            out.println("HTTP/1.0 200");
            out.println("Content-type: text/html");
            out.println("Server-name: JavaSimpleWebServer");
            final int respLength = RESPONSE.length();
            out.println("Content-length: " + respLength);
            out.println("");
            out.println(RESPONSE);
            out.flush();
        } catch (final IOException e) {
            final String eMessage = e.getMessage();
            System.out.println("Failed respond to client request: " + eMessage);
        }
    }
}