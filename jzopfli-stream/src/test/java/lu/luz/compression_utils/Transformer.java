package lu.luz.compression_utils;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Array;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;

public class Transformer implements ClassFileTransformer {
	private static final String[] TRANSFORM = new String[] { "lu/luz/"};

	@Override
	public byte[] transform(ClassLoader loader, String name, Class<?> clazz, ProtectionDomain domain, byte[] bytes) {
		for (String prefix : TRANSFORM)
			if (!name.startsWith(prefix))
				return bytes;

		CtClass cl = null;
		try {
			cl = ClassPool.getDefault().makeClass(new ByteArrayInputStream(bytes));
			if (!cl.isInterface()) {
				CtField field = CtField.make("private static final org.slf4j.Logger _log;", cl);
				String value = "org.slf4j.LoggerFactory.getLogger(" + name.replace('/', '.') + ".class);";
				cl.addField(field, value);
				for (CtBehavior behavior : cl.getDeclaredBehaviors())
					transformBehavior(behavior);
				bytes = cl.toBytecode();
			}
		} catch (Exception e) {
			System.err.println("Could not instrument  " + name + ",  exception : " + e.getMessage());
			e.printStackTrace(System.err);
		} finally {
			if (cl != null)
				cl.detach();
		}
		return bytes;
	}


	private static void transformBehavior(CtBehavior behavior) throws NotFoundException, CannotCompileException {
		if (behavior.isEmpty())
			return;
		String signature = getSignature(behavior);
		String returnValue = getReturnValue(behavior);
		behavior.insertBefore(Transformer.class.getName()+".increment();");
		behavior.insertBefore("_log.trace("+Transformer.class.getName()+".getPrefix()+" + signature + ");");

		behavior.insertAfter(""+Transformer.class.getName()+".decrement();");
		if(returnValue!=null)
			behavior.insertAfter("_log.trace("+Transformer.class.getName()+".getPrefix()+" + returnValue + ");");
		//behavior.insertAfter("_log.trace(\"<" + signature + returnValue + ");");
	}

	public static final ThreadLocal<AtomicInteger> DEPTH=new ThreadLocal<>();
	static{
		DEPTH.set(new AtomicInteger());
	}
	public static void increment(){
		DEPTH.get().incrementAndGet();
	}
	public static void decrement(){
		DEPTH.get().decrementAndGet();
	}

	public static String getPrefix(){
		Integer depth = DEPTH.get().get();
		StringBuilder sb=new StringBuilder(depth*2+0);
		sb.append(">");
		for(int i=0;i<depth;i++)
			sb.append("  ");
		return sb.toString();
	}

	public static String getPrefixOut(){
		return "<";
	}

	private static String getSignature(CtBehavior behavior) throws NotFoundException {
		CtClass parameterTypes[] = behavior.getParameterTypes();

		CodeAttribute codeAttribute = behavior.getMethodInfo().getCodeAttribute();
		LocalVariableAttribute locals = (LocalVariableAttribute) codeAttribute.getAttribute("LocalVariableTable");


		StringBuilder sb = new StringBuilder("\""+behavior.getName() + "(\" ");
		for (int i = 0; i < parameterTypes.length; i++) {
			if (i > 0)
				sb.append(" + \", \" ");
			sb.append(" + \"");
			sb.append(getParameterName(behavior, locals, i));
			sb.append("\" + \"=");

			CtClass parameterType = parameterTypes[i];
			if(parameterType.isPrimitive())
				sb.append("\"+ $" + (i + 1));
			else
				sb.append("\"+ "+Transformer.class.getName()+".printObject($" + (i + 1)+")");
		}
		sb.append("+\")\"");

		return sb.toString();
	}

	public static String printObject(Object o) {
		if(o==null)
			return "null";
		else if(o instanceof String)
			return  "\""+(String)o+"\"";
		else if(o instanceof Number)
			return o.toString();
		else if(o.getClass().isArray()){
			int length=Array.getLength(o);
			if(length==1){
				return "{"+printObject(Array.get(o,  0))+"}";
			}else{
				String name=o.getClass().getSimpleName();
				return name.substring(0, name.length()-1)+length+"]";
			}
		}
		else
			return o.getClass().getSimpleName();
	}

	private static String getParameterName(CtBehavior behavior, LocalVariableAttribute locals, int i) {
		if (locals == null)
			return Integer.toString(i + 1);
		else if (Modifier.isStatic(behavior.getModifiers()))
			return locals.variableName(i); //has no reference to an instance "this"
		else
			return locals.variableName(i + 1); // skip #0 which is reference to "this"
	}

	private static String getReturnValue(CtBehavior method) throws NotFoundException {
		if(method instanceof CtConstructor)
			return null;
		else{
			CtClass type = ((CtMethod) method).getReturnType();
			if("void".equals(type.getName()))
				return null;
			else
				if(type.isPrimitive())
					return "\"returns: \" + $_ ";
				else
					return "\"returns: \" + "+Transformer.class.getName()+".printObject($_) ";
		}

	}
}
