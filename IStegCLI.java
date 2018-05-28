package isteg;

import java.nio.file.Paths;
import java.util.Scanner;

public class IStegCLI {

    private static final String VCODE = "2.0";
	private static Scanner scanner;

	public static void main(String[] args) {
		System.out.println("iSteg CLI v-"+VCODE+"\nEnter your choice:\n\t1. Hide a file with Steg\n\t2. Hide a message with Steg\n\t3. Extract stuff from Steg\n\tEnter any things to exit.");
		scanner = new Scanner(System.in);
		String ch = scanner.nextLine()+" "; // Avoiding IndexOutOfBoundsException
		char choice = ch.charAt(0);
        switch (choice) {
            case '3':
                readStuff();
                break;
            case '2':
                hideMsg();
                break;
            case '1':
                hideFile();
                break;
            default:
                break;
        }
		
	}

	private static void hideMsg() {
		System.out.println("Enter file name in which your message will be hidden:");
		String topFile = scanner.nextLine();
		System.out.println("Enter Your Message:");
		String msg = scanner.nextLine();
		int bitCount = getBitCount();
		System.out.println("Enter new file name:");
		String newFileName = scanner.nextLine();
                System.out.println("Enter Password (If you want not to encrypt, just press enter):");
                String password = scanner.nextLine();
		newFileName +=".png";
		int err = Steg.write(Paths.get(topFile), msg, bitCount,password.toCharArray(), newFileName);
        switch (err) {
            case Steg.SUCCESS:
                System.out.println("Successfully created steganographic file "+newFileName);
                break;
            case Steg.ERR_NOTANIMAGE:
                System.out.println("The file \""+topFile+"\" doesn\'t contain proper image data");
                break;
            case Steg.ERR_FILEREAD:
                System.out.println("Unable to read file \""+topFile+"\".");
                break;
            case Steg.ERR_FILEWRITE:
                System.out.println("Unable to Write file.");
                break;
            case Steg.ERR_LOWIMGSIZE:
                System.out.println("The image resolution is too low to save all of the data.");
                break;
            case Steg.ERR_CIPHERFAILED:
                System.out.println("Cypher error! Please try Again.");
                break;
            default:
                break;
        }
	}

	private static int getBitCount() {
		int ret = 0;
		do {
			System.out.println("Choose One\n\t1. 1-LSB (Extremely low distruction to image, holds less data)\n\t2. 2-LSB (Low distruction to image, holds more data)(Recommended)");
			String s = scanner.nextLine();
			if(s.length()>0) {
			if(Character.isDigit(s.charAt(0)))
				ret = Integer.parseInt(s.substring(0,1));
			}
		} while(ret!=1 && ret!=2);
		return ret;
	}

	private static void hideFile() {
		System.out.println("Enter file name in which your file will be hidden:");
		String topFile = scanner.nextLine();
		System.out.println("Enter the name of the file u want to hide:");
		String bottomFile = scanner.nextLine();
		int bitCount = getBitCount();
		System.out.println("Enter new file name:");
		String newFileName = scanner.nextLine();
		newFileName +=".png";
                System.out.println("Enter Password (If you want not to encrypt, just press enter):");
                String password = scanner.nextLine();
		int err = Steg.write(Paths.get(topFile), Paths.get(bottomFile), bitCount,password.toCharArray(), newFileName);
        switch (err) {
            case Steg.SUCCESS:
                System.out.println("Successfully created steganographic file "+newFileName);
                break;
            case Steg.ERR_NOTANIMAGE:
                System.out.println("The file \""+topFile+"\" doesn\'t contain proper image data");
                break;
            case Steg.ERR_FILEREAD:
                System.out.println("Unable to read file \""+topFile+"\" or "+"\""+bottomFile+"\"");
                break;
            case Steg.ERR_FILEWRITE:
                System.out.println("Unable to Write file.");
                break;
            case Steg.ERR_LOWIMGSIZE:
                System.out.println("The image resolution is too low to save all of the data.");
                break;
            case Steg.ERR_CIPHERFAILED:
                System.out.println("Cypher error! Please try Again.");
                break;
            default:
                break;
        }
	}

	private static void readStuff() {
		System.out.println("Enter file name with extension: ");
		String name = scanner.nextLine();
                System.out.println("Password (Press enter if the steganographic data wasn\'t encrypted):");
                String password = scanner.nextLine();
                if(!name.endsWith(".png"))
                    name+=".png";
		String[] data = Steg.read(Paths.get(name),password.toCharArray());
        switch (Integer.parseInt(data[0])) {
            case Steg.SUCCESS:
            case Steg.SUCCESS_NOPASS:
                if(data[2].equalsIgnoreCase("1")) {
                    System.out.println("Message Extraction successful. The text is:\n");
                    System.out.println(data[1]);
                }
                else {
                    System.out.println("File Extraction successful. The file name is: "+data[1]);
                }
                if(Integer.parseInt(data[0]) == Steg.SUCCESS_NOPASS)
                    System.out.println("The steganographic data wasn\'t encrypted");
                break;
            case Steg.ERR_NOTANIMAGE:
                System.out.println("The file \""+name+"\" doesn\'t contain proper image data");
                break;
            case Steg.ERR_FILEREAD:
                System.out.println("Unable to read file \""+name+"\"");
                break;
            case Steg.ERR_FILEWRITE:
                System.out.println("Unable to write file.");
                break;
            case Steg.ERR_NOSTEG:
                System.out.println("No steganographic data found on \""+name+"\"");
                break;
            case Steg.ERR_CIPHERFAILED:
                System.out.println("Cipher failed. Please try again.");
                break;
            case Steg.ERR_PASSREQ:
                System.out.println("\""+name+"\""+" contains encrypted steganographic data. Password required.");
                break;
            case Steg.ERR_WRONGPWD:
                System.out.println("Password incorrect. Operation failed.");
                break;
            default:
                break;
        }
		
	}

}
