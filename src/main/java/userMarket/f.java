package userMarket;

public class f {
	public class Main {
	    static int calc(int value) {
	        if (value <= 1) return value;
	        return calc(value - 1) + calc(value - 2);
	    }
	    static int calc(String str) {
	        int value = Integer.valueOf(str);
	        if (value <= 1) return value;
	        return calc(value - 1) + calc(value - 3);
	    }
	    public static void main(String[] args) {
	        System.out.print(calc("5"));
	    }
	}
}
