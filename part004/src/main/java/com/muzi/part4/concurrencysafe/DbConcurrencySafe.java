package com.muzi.part4.concurrencysafe;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 确保db并发修改数据安全型
 * <br/> <br/>
 * <strong>数据修改一般由3个步骤组成，必须将这3个步骤放在callback中才可确保并发修改的安全性</strong>
 * <ol>
 *     <li>从db中获取数据</li>
 *     <li>内存中修改数据</li>
 *     <li>将数据写入db</li>
 * </ol>
 *
 *
 */
public interface DbConcurrencySafe {

    /**
     * 对同一个key，此方法可以确保 callback 中修改db数据的安全性<br/><br/>
     * <b>注意</b>
     * <ol>
     * <li>对于同一个key，不支持嵌套调用</li>
     * <li>必须将数据修改的3个步骤(<b>获取、修改、保存</b>)全部放在callback中执行才可确保修改的安全性</li>
     * </ol>
     *
     * @param key
     * @param callback
     * @param successCallBack 成功后将回调 successCall，参数为callback返回的结果
     * @param failCallBack    失败后将回调
     */
    <T> T exec(String key, Supplier<T> callback, Consumer<T> successCallBack, Consumer<ConcurrencyFailException> failCallBack);

    /**
     * 对于po中的同一条记录(id对应的记录)，此方法可以确保 callback 中修改db数据的安全性 <br/><br/>
     * <b>注意</b>
     * <ol>
     * <li>对于同一条记录，不支持嵌套调用</li>
     * <li>必须将数据修改的3个步骤(<b>获取、修改、保存</b>)全部放在此方法中执行才可确保修改的安全性</li>
     * </ol>
     *
     * @param po
     * @param callback
     * @param successCallBack 成功后将回调 successCall，参数为callback返回的结果
     * @param failCallBack    失败后将回调
     */
    default <T> T exec(Class<?> po, String id, Supplier<T> callback, Consumer<T> successCallBack, Consumer<ConcurrencyFailException> failCallBack) {
        return exec(String.format("%s:%s", po.getName(), id), callback, successCallBack, failCallBack);
    }

    /**
     * 对于po中的同一条记录(id对应的记录)，此方法可以确保 callback 中修改db数据的安全性 <br/><br/>
     * <b>注意</b>
     * <ol>
     * <li>对于同一条记录，不支持嵌套调用</li>
     * <li>必须将数据修改的3个步骤(<b>获取、修改、保存</b>)全部放在此方法中执行才可确保修改的安全性</li>
     * </ol>
     *
     * @param po
     * @param callback
     */
    default <T> T exec(Class<?> po, String id, Supplier<T> callback) {
        return exec(String.format("%s:%s", po.getName(), id), callback, null, null);
    }

}
