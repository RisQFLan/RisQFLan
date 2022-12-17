package atree.core.tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;

public class TestLoadBenchmarks {

	public static void testBenchmarks() {
		System.out.println( "Hello World!" );
		File dot = new File("benchmarks/and-or/And-Or/and.dot");
		try {
			MutableGraph loadedDot = new Parser().read(dot);
			Collection<MutableNode> nodes = loadedDot.nodes();
			for(MutableNode node : nodes) {
				System.out.println(node);
			}
			System.out.println(loadedDot.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		testBenchmarks();
	}
	
}
