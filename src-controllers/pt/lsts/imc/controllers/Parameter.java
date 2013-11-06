package pt.lsts.imc.controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter {

	/**
	 * @return The name of the field (to be presented by the GUI). If not set, the name of the
	 * variable will be used.
	 */
    String name() default "";
    
    /**
	 * The description will provide further information on this field (seen in the configuration panel)
	 * @return The description of the field. If not set, the <b>name</b> parameter will be used.
	 */
    String description() default "";
}
