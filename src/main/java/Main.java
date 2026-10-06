import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.io.*;
import java.util.zip.Inflater;

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
				try {
					readFile(args[2]);
				} catch (Exception e) {
                    throw new RuntimeException(e);
                }
			}
			default -> System.out.println("Unknown command: " + command);
		}
	}

	public static void readFile(String hash) throws Exception {
		File f = getObjectFile(hash);
		if (!f.exists()) {
			throw new FileNotFoundException();
		}

		StringBuilder result = decompressGitFile(f);
		System.out.print(result.toString());
	}

	public static File getObjectFile(String hash) {
		String path = ".git/objects/" +
				hash.substring(0,2) + "/" +
				hash.substring(2);
		File f = new File(path);
		if (!f.exists() && !f.isDirectory()) {
			return new File("");
		}
		return f;
	}

	public static StringBuilder decompressGitFile(File f) {
		Inflater inflater = new Inflater();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		try (InputStream fileStream = new FileInputStream(f)) {
			inflater.setInput(fileStream.readAllBytes());

			while (!inflater.finished()) {
				int decompressedSize = inflater.inflate(buffer);
				outputStream.write(buffer, 0, decompressedSize);
			}

			StringBuilder result = new StringBuilder();
			boolean print = false;
			for (byte b : buffer) {
				if (print && b == 0x00) {
					break;
				}
				if (b == 0x00) {
					print = true;
					continue;
				}


				if (print) {
					result.append((char)b);
				}
			}
			return result;
		} catch (Exception e) {
            return new StringBuilder();
        }
	}
}