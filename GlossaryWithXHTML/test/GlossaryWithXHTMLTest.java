import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

public class GlossaryWithXHTMLTest {

    /*
     * numOfWords tests
     */
    @Test
    public void numOfWordstest_example7() {
        String file = "data/example.txt";
        int words = Main.numOfWords(file);

        assertEquals(7, words);
    }

    @Test
    public void numOfWordstest_5() {
        String file = "data/example5.txt";
        int words = Main.numOfWords(file);

        assertEquals(5, words);
    }

    @Test
    public void numOfWordstest_0() {
        String file = "data/zeroWords.txt";
        int words = Main.numOfWords(file);

        assertEquals(0, words);
    }

    /*
     * getWords tests
     */
    @Test
    public void getWords7() {
        Queue<String> words = Main.getWords("data/example.txt");
        Queue<String> correctWords = new Queue1L<>();
        correctWords.enqueue("book");
        correctWords.enqueue("definition");
        correctWords.enqueue("glossary");
        correctWords.enqueue("language");
        correctWords.enqueue("meaning");
        correctWords.enqueue("term");
        correctWords.enqueue("word");

        assertEquals(correctWords, words);
    }

    @Test
    public void getWords5() {
        Queue<String> words = Main.getWords("data/example5.txt");
        Queue<String> correctWords = new Queue1L<>();
        correctWords.enqueue("book");
        correctWords.enqueue("definition");
        correctWords.enqueue("meaning");
        correctWords.enqueue("term");
        correctWords.enqueue("word");

        assertEquals(correctWords, words);
    }

    /*
     * createDefinitionPages test
     */
    @Test
    public void createDefinitionPages_7pages() {
        Main.createDefinitionPages(7, "data/example.txt", "data");

        for (int i = 0; i < 7; i++) {
            String[] words = { "book", "definition", "glossary", "language",
                    "meaning", "term", "word" };
            String right = "data/exampleCorrect" + words[i] + ".html";
            String testing = "data/" + words[i] + ".html";
            SimpleReader correct = new SimpleReader1L(right);
            SimpleReader test = new SimpleReader1L(testing);

            while (!correct.atEOS()) {
                String correctLine = correct.nextLine();
                String testLine = test.nextLine();
                assertEquals(correctLine, testLine);
            }

            correct.close();
            test.close();
        }
    }

    /*
     * creatIndexPage test
     */
    @Test
    public void createIndexPageTest_7() {

        Main.createIndexPage(7, "data/example.txt", "data");

        SimpleReader correct = new SimpleReader1L("data/correctIndex.html");
        SimpleReader test = new SimpleReader1L("data/index.html");

        while (!correct.atEOS()) {
            String correctLine = correct.nextLine();
            String testLine = test.nextLine();
            assertEquals(correctLine, testLine);
        }

        correct.close();
        test.close();
    }
}
