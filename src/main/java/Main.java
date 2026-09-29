import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		final String command = args[0];
		switch (command) {
			case "init" -> {
				final File root = new File(".git");
				new File(root, "objects").mkdirs();
				new File(root, "refs").mkdirs();
				final File head = new File(root, "HEAD");
				try {
					head.createNewFile();
					Files.write(head.toPath(), "ref: refs/heads/main\n".getBytes());
					System.out.println("Initialized git directory");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			case "cat-file" -> {

				String prefix = args[1].substring(0,2);
				Path dir = Paths.get(".git/objects/"+prefix+"/");
				System.out.println(".git/objects/"+prefix+"/");

				try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
					for (Path file: stream) {
						String fileName = file.getFileName();
						if (fileName != args[1]) {
							continue;
						}

						System.out.println(file.getFileName());
					}

				} catch (IOException | DirectoryIteratorException x) {
					System.err.println(x);
				}

			}
			default -> System.out.println("Unknown command: " + command);
		}
	}
}
