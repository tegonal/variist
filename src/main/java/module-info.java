module com.tegonal.variist {
	requires transitive kotlin.stdlib;
	requires transitive kotlin.reflect;
	requires org.junit.jupiter.params;
	requires org.junit.platform.commons;
	requires ch.tutteli.kbox;
	requires java.logging;
	// from the export of junit classes
	requires org.apiguardian.api;

	exports com.tegonal.variist.config;
	exports com.tegonal.variist.generators;
	exports com.tegonal.variist.providers;
	exports com.tegonal.variist.utils;
}
