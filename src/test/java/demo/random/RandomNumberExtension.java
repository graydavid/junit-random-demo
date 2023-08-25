package demo.random;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedFields;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.ModifierSupport;

class RandomNumberExtension implements BeforeAllCallback, TestInstancePostProcessor, ParameterResolver {

  private final java.util.Random random = new java.util.Random(System.nanoTime());

  /**
   * Inject a random integer into static fields that are annotated with {@code @Random} and can be assigned an integer
   * value.
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    injectFields(testClass, null, ModifierSupport::isStatic);
  }

  /**
   * Inject a random integer into non-static fields that are annotated with {@code @Random} and can be assigned an
   * integer value.
   */
  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    injectFields(testClass, testInstance, ModifierSupport::isNotStatic);
  }

  /**
   * Determine if the parameter is annotated with {@code @Random} and can be assigned an integer value.
   */
  @Override
  public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
    return pc.isAnnotated(Random.class) && isInteger(pc.getParameter().getType());
  }

  /**
   * Resolve a random integer.
   */
  @Override
  public Integer resolveParameter(ParameterContext pc, ExtensionContext ec) {
    return this.random.nextInt();
  }

  private void injectFields(Class<?> testClass, Object testInstance, Predicate<Field> predicate) {

    predicate = predicate.and(field -> isInteger(field.getType()));
    findAnnotatedFields(testClass, Random.class, predicate).forEach(field -> {
      try {
        field.setAccessible(true);
        field.set(testInstance, this.random.nextInt());
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    });
  }

  private static boolean isInteger(Class<?> type) {
    return int.class.isAssignableFrom(type);
  }

}
