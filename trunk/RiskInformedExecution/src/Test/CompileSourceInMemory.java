package Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

public class CompileSourceInMemory {
	
	
   public static void main(String[] args) throws IOException {
      String className = "FaultCondition_null_FollowUp";
      String methodName = "execute";
      String sourceCode = "import java.util.*;import java.math.*;public class FaultCondition_null_FollowUp {public static Double execute(Object[] o) {Object FollowUptimes = o[0];return execute2((Object) FollowUptimes);}private static Double execute2(Object FollowUptimes) {List[Long] list = (List[Long]) FollowUptimes;int errors = 0;Long start = null;for(Long l : list) {if(start == null) {start = l;}else {long d = (l - start);if(d > 1296000000){errors++;}}}if(errors > 0) return true;else return false;}}";
      for (int i = 0; i < 5; i++) {
         try {
            System.out.println("Loop " + i);
            Object a = Class.forName(className).getDeclaredMethod(methodName, new Class[] {Object[].class}).invoke(null, (Object) new Object[] {"a", "ggg"});
            System.out.println("result "+a);
         }
         catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e);
            i--;
            compile(sourceCode);
         }
         catch (NoSuchMethodException e) {
            System.err.println("No such method: " + e);
         }
         catch (IllegalAccessException e) {
            System.err.println("Illegal access: " + e);
         }
         catch (InvocationTargetException e) {
            System.err.println("Invocation target: " + e);
         }
      }
   }

   public static void compile(String sourceCode) {
      System.err.println("Compile");
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

      StringWriter writer = new StringWriter();
      PrintWriter out = new PrintWriter(writer);
      out.println(sourceCode);
      out.close();
      JavaFileObject file = new JavaSourceFromString("FaultCondition_null_FollowUp", writer.toString());

      final Iterable<String> options = Arrays.asList( new String[] { "-d", "/home/stormfire/Dropbox/workspace/RiskInformedExecution/bin/"} );
      Iterable <? extends JavaFileObject > compilationUnits = Arrays.asList(file);
      CompilationTask task = compiler.getTask(null, null, diagnostics, options, null, compilationUnits);

      boolean success = task.call();
   }
}

class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
//		super(URI.create(name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}
