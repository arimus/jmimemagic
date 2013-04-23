package net.sf.jmimemagic;

import junit.framework.*;
import java.io.File;

public class MagicTest extends TestCase {
	private static String gifFile = "test_docs/test.gif";
	private static String pngFile = "test_docs/test.png";
	private static String jpgFile = "test_docs/test.jpg";
	private static String textFile = "test_docs/test.txt";
	private static String word2KFile = "test_docs/test_word_2000.doc";
	private static String word95File = "test_docs/test_word_6.0_95.doc";
	private static String rtfFile = "test_docs/test.rtf";
	private static String excel2KFile = "test_docs/test_excel_2000.xls";
	private static String pdfFile = "test_docs/test.pdf";
	private static String psFile = "test_docs/test.ps";
	private static String javaClass12File = "test_docs/test_1.2.class";
	private static String javaClass13File = "test_docs/test_1.3.class";
	private static String javaClass14File = "test_docs/test_1.4.class";
	private static String mp3_128_44_jstereoFile = "test_docs/test_128_44_jstereo.mp3";
	private static String wavFile = "test_docs/test.wav";
	private static String odtFile = "test_docs/test.odt";
	private static String zipFile = "test_docs/test.zip";

	public static void main(String args[]) {
		junit.textui.TestRunner.run(MagicTest.class);
	}

	// IMAGE TESTS

	public void testGIF() {
		System.out.print("\ntesting GIF image...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(gifFile), true, false);
			if (match != null) {
				assertEquals("image/gif", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testGIF()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testGIF(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testGIF(). message: " + e.getMessage());
		}
	}

	public void testPNG() {
		System.out.print("\ntesting PNG image...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(pngFile), true, false);
			if (match != null) {
				assertEquals("image/png", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testPNG()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testPNG(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testPNG(). message: " + e.getMessage());
		}
	}

	public void testJPEG() {
		System.out.print("\ntesting JPEG image...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(jpgFile), true, false);
			if (match != null) {
				assertEquals("image/jpeg", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testJPEG()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testJPEG(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testJPEG(). message: " + e.getMessage());
		}
	}

	// DOCUMENT TESTS

	public void testText() {
		System.out.print("\ntesting Plain Text Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(textFile), true, false);
			if (match != null) {
				assertEquals("text/plain", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testText()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testText(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testText(). message: " + e.getMessage());
		}
	}

	public void testWord2K() {
		System.out.print("\ntesting Word 2000 Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(word2KFile), true, false);
			if (match != null) {
				assertEquals("application/msword", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testWord2K()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testWord2K(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testWord2K(). message: " + e.getMessage());
		}
	}

	public void testWord95() {
		System.out.print("\ntesting Word 95 Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(word95File), true, false);
			if (match != null) {
				assertEquals("application/msword", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testWord95()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testWord95(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testWord95(). message: " + e.getMessage());
		}
	}

	public void testRTF() {
		System.out.print("\ntesting RTF Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(rtfFile), true, false);
			if (match != null) {
				assertEquals("text/rtf", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testRTF()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testRTF(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testRTF(). message: " + e.getMessage());
		}
	}

	public void testExcel2K() {
		System.out.print("\ntesting Excel 2000 Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(excel2KFile), true, false);
			if (match != null) {
				assertEquals("application/msword", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testExcel2K()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testExcel2K(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testExcel2K(). message: " + e.getMessage());
		}
	}

	// OTHER

	public void testPDF() {
		System.out.print("\ntesting PDF Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(pdfFile), true, false);
			if (match != null) {
				assertEquals("application/pdf", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testPDF()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testPDF(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testPDF(). message: " + e.getMessage());
		}
	}

	public void testPostscript() {
		System.out.print("\ntesting Postscript Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(psFile), true, false);
			if (match != null) {
				assertEquals("application/postscript", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testPostscript()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testPostscript(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testPostscript(). message: " + e.getMessage());
		}
	}

	public void testJavaClass12() {
		System.out.print("\ntesting Java Class File (v1.2)...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(javaClass12File), true, false);
			if (match != null) {
				assertEquals("application/java", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testJavaClass12()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testJavaClass12(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testJavaClass12(). message: " + e.getMessage());
		}
	}

	public void testJavaClass13() {
		System.out.print("\ntesting Java Class File (v1.3)...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(javaClass13File), true, false);
			if (match != null) {
				assertEquals("application/java", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testJavaClass13()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testJavaClass13(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testJavaClass13(). message: " + e.getMessage());
		}
	}

	public void testJavaClass14() {
		System.out.print("\ntesting Java Class File (v1.4)...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(javaClass14File), true, false);
			if (match != null) {
				assertEquals("application/java", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testJavaClass14()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testJavaClass14(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testJavaClass14(). message: " + e.getMessage());
		}
	}

	public void testMP3() {
		System.out.print("\ntesting MPEG Layer 3...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(mp3_128_44_jstereoFile), true, false);
			if (match != null) {
				assertEquals("audio/mp3", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testMP3()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testMP3(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testMP3(). message: " + e.getMessage());
		}
	}

	public void testWave() {
		System.out.print("\ntesting WAVE...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(wavFile), true, false);
			if (match != null) {
				assertEquals("RIFF (little-endian) data", match.getDescription());
			} else {
				System.out.print("failed");
				fail("no match in testWave()");
			}
			assertTrue(match.descriptionMatches("Microsoft PCM"));
			assertTrue(match.descriptionMatches("stereo"));
			assertTrue(match.mimeTypeMatches("application/x-wav"));
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testWave(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testWave(). message: " + e.getMessage());
		}
	}
	public void testOdt(){
		System.out.print("\ntesting ODT Document...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(odtFile), true, false);
			if (match != null) {
				assertEquals("application/vnd.oasis.opendocument.text", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testOdt()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testOdt(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testOdt(). message: " + e.getMessage());
		}
		
	}
	
	public void testZip(){
		System.out.print("\ntesting Zip File...");
		try {
			MagicMatch match = Magic.getMagicMatch(new File(zipFile), true, false);
			if (match != null) {
				assertEquals("application/zip", match.getMimeType());
			} else {
				System.out.print("failed");
				fail("no match in testOdt()");
			}
			System.out.print("ok");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception in testZip(). message: " + e);
		} catch (Error e) {
			e.printStackTrace();
			fail("error in testZip(). message: " + e.getMessage());
		}
		
	}
}
