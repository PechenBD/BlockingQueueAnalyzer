import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        Thread generator = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queue1.put(text);
                    queue2.put(text);
                    queue3.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threads.add(generator);
        generator.start();

        Runnable counterA = () -> {
            counterLogic(queue1, 'a');
        };

        Runnable counterB = () -> {
            counterLogic(queue2, 'b');
        };

        Runnable counterC = () -> {
            counterLogic(queue3, 'c');
        };

        Thread letterA = new Thread(counterA);
        threads.add(letterA);
        letterA.start();

        Thread letterB = new Thread(counterB);
        threads.add(letterB);
        letterB.start();

        Thread letterC = new Thread(counterC);
        threads.add(letterC);
        letterC.start();

        for (Thread thread : threads) {
            thread.join();
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void counterLogic(BlockingQueue<String> queue, char letter) {
        int maxCount = 0;
        String maxText = " ";
        for (int i = 0; i < 10_000; i++) {
            try {
                int count = 0;
                String text = queue.take();
                for (int j = 0; j < text.length(); j++) {
                    if (text.charAt(j) == letter) {
                        count++;
                    }
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Максимальное колличество символов " + letter + " (" + maxCount + " шт.)" + " в строчке:");
        System.out.println(maxText);
        System.out.println();
    }
}