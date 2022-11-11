package tp1.p2.pruebas.parte1;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import tp1.p2.PlantsVsZombies;

public class PlantsVsZombiesTests {

	private static boolean compareOutput(Path expectedPath, Path actualPath) throws FileNotFoundException, IOException {
		boolean same = true;
		try (BufferedReader expected = new BufferedReader(new FileReader(expectedPath.toFile()));
				BufferedReader actual = new BufferedReader(new FileReader(actualPath.toFile()))) {

			String expectedLine = expected.readLine();
			String actualLine = actual.readLine();
			int lineNumber = 1;
			while (same && expectedLine != null && actualLine != null) {
				same = expectedLine.equals(actualLine);
				if (!same) {
					System.out.println("Line: %d".formatted(lineNumber));
					System.out.println("Expected: %s".formatted(expectedLine));
					System.out.println("Actual: %s".formatted(actualLine));
				}
				expectedLine = expected.readLine();
				actualLine = actual.readLine();
				lineNumber++;
			}

			same = same && expectedLine == null && actualLine == null;
		}
		return same;
	}

	public void parameterizedTest(Path input, Path expected, Path output, String[] args) {
		try (PrintStream out = new PrintStream(output.toFile()); InputStream in = new FileInputStream(input.toFile())) {
			PrintStream oldOut = System.out;
			InputStream oldIn = System.in;

			System.setOut(out);
			System.setIn(in);

			PlantsVsZombies.main(args);

			System.setOut(oldOut);
			System.setIn(oldIn);

			if (!compareOutput(expected, output)) {
				fail();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void test00() {
		parameterizedTest(Paths.get("test/p21/00-easy_25-input.txt"), Paths.get("test/p21/00-easy_25-expected.txt"),
				Paths.get("test/p21/00-easy_25-output.txt"), new String[] { "EASY", "25" });
	}

	@Test
	public void test01() {
		parameterizedTest(Paths.get("test/p21/01-easy_25-input.txt"), Paths.get("test/p21/01-easy_25-expected.txt"),
				Paths.get("test/p21/01-easy_25-output.txt"), new String[] { "EASY", "25" });
	}

	@Test
	public void test02() {
		parameterizedTest(Paths.get("test/p21/02-easy_25-input.txt"), Paths.get("test/p21/02-easy_25-expected.txt"),
				Paths.get("test/p21/02-easy_25-output.txt"), new String[] { "EASY", "25" });
	}

	@Test
	public void test03() {
		parameterizedTest(Paths.get("test/p21/03-hard_17-input.txt"), Paths.get("test/p21/03-hard_17-expected.txt"),
				Paths.get("test/p21/03-hard_17-output.txt"), new String[] { "HARD", "17" });
	}

	@Test
	public void test04() {
		parameterizedTest(Paths.get("test/p21/04-insane_360-input.txt"), Paths.get("test/p21/04-insane_360-expected.txt"),
				Paths.get("test/p21/04-insane_360-output.txt"), new String[] { "INSANE", "360" });
	}

}
