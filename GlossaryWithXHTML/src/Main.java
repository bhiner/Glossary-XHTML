import java.util.Comparator;

import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This program takes the input text file and creates a glossary containing the
 * words and definitions in the inputed text file with links to other words in
 * glossary if they are located in the definition.
 *
 * @author Benjamin Hiner
 *
 */
public final class Main {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private Main() {
    }

    /**
     * @param file
     *            input text file
     * @return the number of words with definitions
     */
    public static int numOfWords(String file) {
        int numOfWords = 0;
        SimpleReader input = new SimpleReader1L(file);

        //Count number of inputs by counting empty lines
        while (!input.atEOS()) {
            if (input.nextLine().equals("")) {
                numOfWords++;
            }
        }

        input.close();
        return numOfWords;
    }

    /**
     * Returns a negative integer, zero, or a positive integer as the first word
     * is less than, equal to, or greater than the second in terms of alphabetic
     * order.
     */
    private static class Alphabetic implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    /**
     * @param file
     *            input text file
     *
     * @return a queue of the words to be defined in alphabetic order
     * @requires file /= {}
     * @ensures Queue<String> is in alphabetic order going from left(front) to
     *          right(back)
     */
    public static Queue<String> getWords(String file) {
        Queue<String> wordList = new Queue1L<>();

        //Open file and find all words
        SimpleReader inputFile = new SimpleReader1L(file);
        String word = inputFile.nextLine();
        wordList.enqueue(word);
        while (!inputFile.atEOS()) {
            String line = inputFile.nextLine();
            if (line.equals("") && !inputFile.atEOS()) {
                line = inputFile.nextLine();
                wordList.enqueue(line);
            }
        }

        //Sort words in Queue into alphabetic order
        Comparator<String> alphabetic = new Alphabetic();
        wordList.sort(alphabetic);

        inputFile.close();

        return wordList;
    }

    /**
     * @param inputFile
     *            file where word definition page is
     * @param file
     *            input text file
     * @return the definition of the word as a string
     * @requires inputFile to be open
     */
    public static String getDefinition(SimpleReader inputFile, String file) {
        StringBuilder definition = new StringBuilder();
        StringBuilder word = new StringBuilder();

        //Create set of characters that should not be part of word
        Set<Character> symbols = new Set1L<>();
        symbols.add(' ');
        symbols.add(',');
        symbols.add('.');
        symbols.add(':');

        //Find next word
        String input = inputFile.nextLine();
        while (!input.equals("")) {
            for (int i = 0; i < input.length(); i++) {
                if (!symbols.contains(input.charAt(i))) {
                    word.append(input.charAt(i));
                }
                if (symbols.contains(input.charAt(i))
                        || (i == input.length() - 1)) {
                    boolean contains = false;
                    //Compare word to words in input file
                    Queue<String> words = getWords(file);
                    while (words.length() > 0) {
                        String currentWord = words.dequeue();
                        //If word is in input file then add link to definition
                        if (currentWord.equals(word.toString())) {
                            definition.append("<a href=\"");
                            definition.append(word);
                            definition.append(".html\">");
                            definition.append(word);
                            definition.append("</a>");
                            if (i != (input.length() - 1)) {
                                definition.append(input.charAt(i));
                            }
                            contains = true;
                        }
                    }
                    //If not in input file then add to definition without link
                    if (!contains) {
                        definition.append(word);
                        if (i != input.length() - 1) {
                            definition.append(input.charAt(i));
                        }
                    }
                    //Clear StringBuilder so next word can be added
                    word.delete(0, word.length());
                }
            }
            input = inputFile.nextLine();
        }
        return definition.toString();

    }

    /**
     * @param numOfWords
     *            number of words being defined
     * @param file
     *            input text file
     * @param folder
     *            output folder
     * @requires folder to exist
     */
    public static void createDefinitionPages(int numOfWords, String file,
            String folder) {
        SimpleReader inputFile = new SimpleReader1L(file);

        for (int i = 0; i < numOfWords; i++) {
            //Find word to define
            String word = inputFile.nextLine();
            String saveLocation = folder + "/" + word + ".html";
            SimpleWriter output = new SimpleWriter1L(saveLocation);

            //Find word definition
            String definition = getDefinition(inputFile, file);

            //Add header
            output.print("<html>\n<head>\n<title>");
            output.print(word);
            output.print("</title>\n</head>");

            //Add body
            output.print("\n<body>\n<h2><b><i><font color=\"red\">");
            output.print(word);
            output.print("</font></i></b></h2>");

            //Add definition
            output.print("\n<blockquote>");
            output.print(definition);
            output.print("</blockquote>\n<hr />");

            //Add return link
            output.print(
                    "\n<p>Return to <a href=\"index.html\">index</a>.</p>");

            //Close page
            output.print("\n</body>\n</html>");

            //Close file
            output.close();
        }

        inputFile.close();
    }

    /**
     * @param numOfWords
     *            number of words being defined
     * @param file
     *
     *            input text file
     * @param folder
     *            output folder
     * @requires folder to exist
     */
    public static void createIndexPage(int numOfWords, String file,
            String folder) {
        SimpleWriter output = new SimpleWriter1L(folder + "/index.html");
        SimpleReader inputFile = new SimpleReader1L(file);
        Queue<String> wordList = getWords(file);

        //Add header
        output.print("<html>\n<head>\n<title>Glossary</title>\n</head>");

        //Add body
        output.print("\n<body>\n<h2>Glossary</h2>\n<hr />\n<h3>Index</h3>");

        //Add links
        output.print("\n<ul>");
        for (int i = 0; i < numOfWords; i++) {
            String word = wordList.dequeue();
            output.print("\n<li><a href=\"");
            output.print(word);
            output.print(".html\">");
            output.print(word);
            output.print("</a></li>");
        }
        output.print("\n</ul>");

        //Close page
        output.print("\n</body>\n</html>");

        //Close file
        output.close();
        inputFile.close();
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        //Input file
        out.print("Please enter input file: ");
        String file = in.nextLine();

        //Output folder
        out.print("Please enter an exsisting output folder: ");
        String folder = in.nextLine();

        //Get number of words in file
        int numOfWords = numOfWords(file);

        //Create definition pages
        createDefinitionPages(numOfWords, file, folder);

        //Create index page
        createIndexPage(numOfWords, file, folder);

        //Ran successfully
        out.println("Run complete");

        in.close();
        out.close();
    }

}
