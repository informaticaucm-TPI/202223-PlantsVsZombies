package tp1.p2.pruebas.parte2;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import tp1.p2.PlantsVsZombies;

public class RegeneraSalidasTests {

	public void parameterizedTest(Path input, Path output, String[] args) {
		try (PrintStream out = new PrintStream(output.toFile()); InputStream in = new FileInputStream(input.toFile())) {
			PrintStream oldOut = System.out;
			InputStream oldIn = System.in;

			System.setOut(out);
			System.setIn(in);

			PlantsVsZombies.main(args);

			System.setOut(oldOut);
			System.setIn(oldIn);

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
		parameterizedTest(Paths.get("test/p22/00-easy_25-input.txt"), Paths.get("test/p22/00-easy_25-expected.txt"),
				new String[] { "EASY", "25" });
	}

	@Test
	public void test01() {
		parameterizedTest(Paths.get("test/p22/01-easy_25-input.txt"), Paths.get("test/p22/01-easy_25-expected.txt"),
				new String[] { "EASY", "25" });
	}

	@Test
	public void test02() {
		parameterizedTest(Paths.get("test/p22/02-easy_25-input.txt"), Paths.get("test/p22/02-easy_25-expected.txt"),
				new String[] { "EASY", "25" });
	}

	@Test
	public void test03() {
		parameterizedTest(Paths.get("test/p22/03-hard_17-input.txt"), Paths.get("test/p22/03-hard_17-expected.txt"),
				new String[] { "HARD", "17" });
	}

	@Test
	public void test04() {
		parameterizedTest(Paths.get("test/p22/04-insane_360-input.txt"), Paths.get("test/p22/04-insane_360-expected.txt"),
				new String[] { "INSANE", "360" });
	}

}
