package pe.edu.utec.hung.util;

import java.util.LinkedList;

public class Alphabet {

	public static final String[] asArray = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
			"m", "n", "ñ", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

	// Order based in probability of occurrence in current corpus
	// There are 499 letters in corpus, if letter "a" appears 60 times, then 60/499 = 0.1202, hence player2 will attempt it first
	public static final LinkedList<String> asLinkedList() {
		LinkedList<String> ll = new LinkedList<String>();
		ll.add("a"); // 0.120240481
		ll.add("e"); // 0.112224449
		ll.add("i"); // 0.094188377
		ll.add("o"); // 0.076152305
		ll.add("l"); // 0.070140281
		ll.add("r"); // 0.070140281
		ll.add("n"); // 0.062124248
		ll.add("c"); // 0.044088176
		ll.add("d"); // 0.034068136
		ll.add("m"); // 0.034068136
		ll.add("p"); // 0.032064128
		ll.add("s"); // 0.03006012
		ll.add("b"); // 0.022044088
		ll.add("u"); // 0.022044088
		ll.add("t"); // 0.02004008
		ll.add("f"); // 0.014028056
		ll.add("g"); // 0.01002004
		ll.add("h"); // 0.006012024
		ll.add("j"); // 0.006012024
		ll.add("k"); // 0.004008016
		ll.add("x"); // 0.004008016
		ll.add("z"); // 0.004008016
		ll.add("q"); // 0.002004008
		//ll.add("ñ"); // 0
		//ll.add("w"); // 0
		//ll.add("y"); // 0

		return ll;

	}
}
