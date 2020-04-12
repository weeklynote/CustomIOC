IOC:控制反转，用于描述对象创建与管理问题。IOC容器用来创建对象，而不是通过new，所以叫做控制反转。IOC很好的解决了对象耦合的问题。
IOC与DI(Dependance Injection)本质上说的是同一个事情，前者从对象的角度出发；后者从容器的角度出发。
AOP面向切面编程是OOP的延续，OOP能解决垂直层面的代码复用问题，但是对于水平方向的代码重复(比如事务和打印日志等)就不能解决代码复用问题，需要在每个方法前面增加很多重复代码。AOP可以在不改变原有逻辑情况下对方法进行增强，其核心为使用动态代理生成代理对象，在invoke方法中对原有代码进行增强。
针对事务的控制，是针对的Connection而言。
Java动态代理需要类实现接口；Cglib方式实现动态代理对类本身没有特殊要求。

BeanFactory:创建Bean的工厂类顶级接口；
FactoryBean：创建特殊Bean的工厂类，需要自定义实例化和属性等设置。

@Bean一般针对方法，表示改方法的返回值用于容器管理。
@Component一般针对类而言，表示创建该类的实例用于容器管理。

脏读：一个线程中的事务读取到了另一个线程未提交的数据。
不可重复读：一个线程中的事务读到了另一个线程中已经提交的update数据(前后不一致)。
幻读：一个线程中的事务读到了另一个线程中已经提交的insert或delete数据。

ACID与实务隔离级别：
Serializable：可避免脏读。不可重复读、幻读发生，效率最低。
Reapeatable read：可避免脏读、不可重复读发生，但是有可能发生幻读，是MySql的默认隔离级别。
Read Commit：可变脏读情况发生，有可能发生重复读和幻读。
Read Uncommit：最低级别，以上情况均不能保证。


事务的传播行为：
Service层方法A调用Service层方法B时，A、B方法都添加了事务，那么A调用B时，就需要对事务进行协商，这叫做事务的传播行为。
PROPAGATION_REQUIRED
如果当前没有事务，就新建⼀个事务，如果已经存在⼀个事务中，加⼊到这个事务中。这是最常⻅的选择。
PROPAGATION_SUPPORTS 支持当前事务，如果当前没有事务，就以非事务方式执行。
PROPAGATION_MANDATORY 使⽤当前的事务，如果当前没有事务，就抛出异常。
PROPAGATION_REQUIRES_NEW 新建事务，如果当前存在事务，把当前事务挂起。
PROPAGATION_NOT_SUPPORTED 以非事务⽅式执行操作，如果当前存在事务，就把当前事务挂起。
PROPAGATION_NEVER 以非事务方式执行，如果当前存在事务，则抛出异常。
PROPAGATION_NESTED 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则
执行与PROPAGATION_REQUIRED类似的操作。