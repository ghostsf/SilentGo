# SilentGo Framework
a light web mvc framework.
#### How to Use
add maven dependency
```
<dependency>
    <groupId>com.silentgo</groupId>
    <artifactId>framework</artifactId>
    <version>0.0.8</version>
</dependency>
```



####  IOC
use with @Inject  
####  AOP 
class implements Interceptor.
```java
public interface Interceptor {

    default boolean build(SilentGo me) {
        return true;
    }

    default int priority() {
        return Integer.MIN_VALUE;
    }

    Object resolve(AOPPoint point, boolean[] isResolved) throws Throwable;
}
```
####  route
match path , you can use :   
		
```java
	@Controller(value="/path")  
	@Route(value="/{id}/{name}") or @Route(value="/[0-9a-z]+" , regex = true)
```
match parameters  

```java
	public void index(Response reponse,Resquest request ,@PathVariable(value="id") Integer id ,@PathVariable(value="name") String myname, 
	@RequestParam @RequestString( required = true , range = { 5 , 10}) String name)
```  
validate parameters
	class implements IValidator
	you can custom validator for parameters yourself


