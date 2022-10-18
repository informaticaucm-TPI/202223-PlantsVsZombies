package tp1.utils;

/**
 * @author Simon Pickin
 * @author Ivan Martinez-Ortiz
 */
public class StringUtils {

	public static String repeat(char c, int length) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < length; i++) {
			buffer.append(c);
		}
		return buffer.toString();
	}

	public static String repeat(String string, int length) {
		return string.repeat(length);
	}

	public static String centre(String text, int length) {
		return centre(text, length, ' ');
	}

	public static String centre(String text, int length, char paddingChar) {
		if (length < text.length()) {
			throw new IllegalArgumentException(
					String.format("length must be at least '%d', but is '%d'", text.length(), length));
		}

		int paddingLength = length - text.length();
		int paddingRight = paddingLength / 2;
		int paddingLeft = paddingRight + paddingLength % 2;

		return String.format("%s%s%s", repeat(paddingChar, paddingLeft), text, repeat(paddingChar, paddingRight));
	}

}
