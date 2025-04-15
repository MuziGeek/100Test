-- 创建账户表
create table if not exists t_account
(
    id      varchar(50) primary key comment '账户id',
    name    varchar(50)    not null comment '账户名称',
    balance decimal(12, 2) not null default '0.00' comment '账户余额'
) comment '账户表';

-- 充值记录表
create table if not exists t_recharge
(
    id         varchar(50) primary key comment 'id，主键',
    account_id varchar(50)    not null comment '账户id，来源于表t_account.id',
    price      decimal(12, 2) not null comment '充值金额',
    status     smallint       not null default 0 comment '充值记录状态，0：处理中，1：充值成功',
    version    bigint         not null default 0 comment '系统版本号，默认为0，每次更新+1，用于乐观锁'
) comment '充值记录表';

-- 准备测试数据，
-- 账号数据来一条，
insert ignore into t_account values ('1', '路人', 0);
-- 充值记录来一条，状态为0，稍后我们模拟回调，会将状态置为充值成功
insert ignore into t_recharge values ('1', '1', 100.00, 0, 0);

-- 幂等辅助表
create table if not exists t_idempotent
(
    id             varchar(50) primary key comment 'id，主键',
    idempotent_key varchar(200) not null comment '需要确保幂等的key',
    unique key uq_idempotent_key (idempotent_key)
) comment '幂等辅助表';