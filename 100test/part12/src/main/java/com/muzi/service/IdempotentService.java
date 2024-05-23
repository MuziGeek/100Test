package com.muzi.service;



public interface IdempotentService {
    /**
     * 幂等处理方法，可确保第2个参数中的业务的幂等性
     * 建议第2个参数中的业务是数据库的操作
     *
     * @param idempotentKey 幂等key
     * @param r             需要执行的业务方法
     * @return -1：已处理过，1：本次处理成功
     */
    int idempotent(String idempotentKey, Runnable r);

    /**
     * 幂等处理方法，可确保第2个参数中的业务的幂等性
     * 建议第2个参数中的业务是数据库的操作
     *
     * @param busId   业务id
     * @param busType 业务类型
     * @param r       需要执行的业务方法
     * @return -1：已处理过，1：本次处理成功
     */
    default int idempotent(String busId, String busType, Runnable r) {
        String idempotentKey = String.format("%s:%s", busId, busType);
        return this.idempotent(idempotentKey, r);
    }
}
