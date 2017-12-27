package client.view;

class ThreadSafeOutput {

    synchronized void print(String output) {
        System.out.print(output);
    }
    
    synchronized void println(String output) {
        System.out.println(output);
    }
}
