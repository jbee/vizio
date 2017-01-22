package vizio.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vizio.engine.Constraints.ValueType.text;

import org.junit.Assert;
import org.junit.Test;

import vizio.engine.Constraints.ValueType;

public class TestContraints {

	@Test
	public void textValuesCanBeURLs() {
		assertTrue(text.isValid("http://localhost:8080/"));
		assertTrue(text.isValid("https://www.youtube.com/watch?v=uhU_Uw3Yazo"));
		assertTrue(text.isValid("https://mail.google.com/mail/u/0/#inbox"));
		assertTrue(text.isValid("http://asserttrue.blogspot.se/2010/01/how-to-iimplement-custom-paint-in-50.html#"));
	}
	
	@Test
	public void test() {
		Constraints constraints = Constraints.parse("[color=heat][length=20][users~{foo,bar}]");
		assertEquals(3, constraints.count());
		assertEquals("[color = heat][length = 20][users ~ {foo, bar}]", constraints.toString());
	}
	
	@Test
	public void test2() {
		Constraints constraints = Constraints.parse("[url={jira, GRP-001}][url=https://mail.google.com/mail/u/0/#inbox]");
		assertEquals(2, constraints.count());
	}
}
