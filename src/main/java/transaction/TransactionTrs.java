package transaction;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author MysticalYcc
 * @date 15:19
 * 事务传播行为
 * 所谓事务的传播行为是指，如果在开始当前事务之前，一个事务上下文已经存在，此时有若干选项可以指定一个事务性方法的执行行为。在TransactionDefinition定义中包括了如下几个表示传播行为的常量：
 *
 * 1` TransactionDefinition.PROPAGATION_REQUIRED：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。这是默认值。
 * 2` TransactionDefinition.PROPAGATION_REQUIRES_NEW：创建一个新的事务，如果当前存在事务，则把当前事务挂起。
 * 3` TransactionDefinition.PROPAGATION_SUPPORTS：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。
 * 4` TransactionDefinition.PROPAGATION_NOT_SUPPORTED：以非事务方式运行，如果当前存在事务，则把当前事务挂起。
 * 5` TransactionDefinition.PROPAGATION_NEVER：以非事务方式运行，如果当前存在事务，则抛出异常。
 * 6` TransactionDefinition.PROPAGATION_MANDATORY：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。
 * 7` TransactionDefinition.PROPAGATION_NESTED：如果当前存在事务，则创建一个事务作为当前事务的嵌套事务来运行；如果当前没有事务，
 * 则该取值等价于TransactionDefinition.PROPAGATION_REQUIRED。
 */
@Repository
public class TransactionTrs {

    /**
     * 如果同一个类中有两个事务方法A，B，事务方法A调用事务方法B，B事务方法是不会生效的，因为这样是不走代理类调用的，要想事务奏效必须使用代理类调用。针对此问题有两种方法可以解决
     * 1.将B事务方法放到另外一个类中，通过注入类来进行调用
     * 2.使用AopContext.currentProxy()来进行调用，即在A事务方法中调用B事务方法代码为
     * this对象指代当前对象，非代理对象
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void TransMethodSub(){
        int count = 10;
        TransactionTrs self= (TransactionTrs)AopContext.currentProxy();
        self.TransMethodSon();



    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void TransMethodSon(){

        this.TransMethodSub();
        throw new RuntimeException("被调用者");
    }


    public static void main(String[] args) {

    }

}
