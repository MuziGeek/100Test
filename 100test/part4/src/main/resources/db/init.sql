-- 商品表
create table if not exists t_goods
(
    goods_id   varchar(32) primary key comment '商品id',
    goods_name varchar(256) not null comment '商品名称',
    num        int          not null comment '库存',
    version    bigint default 0 comment '系统版本号'
) comment = '商品表';


-- 并发安全辅助表
create table if not exists t_concurrency_safe
(
    id       varchar(32) primary key comment 'id',
    safe_key varchar(256) not null comment '需要保护的数据的唯一的key',
    version  bigint default 0 comment '系统版本号，默认为0，每次更新+1',
    UNIQUE KEY `uq_safe_key` (`safe_key`)
) comment = '并发安全辅助表';
