-- =============================================
-- 网银项目 bank_demo 预置测试数据
-- =============================================

USE bank_demo;

-- 1. 角色数据
INSERT INTO sys_role (role_name, role_code, description) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有全量后台 CRUD 权限'),
('运营人员', 'OPERATOR', '仅查看产品与申请数据'),
('普通用户', 'USER', '前端登录提交申请');

-- 2. 菜单权限数据
INSERT INTO sys_menu (menu_name, permission, parent_id, path) VALUES
('产品管理', 'product:list', 0, '/admin/product'),
('产品新增', 'product:add', 1, ''),
('产品编辑', 'product:update', 1, ''),
('产品删除', 'product:delete', 1, ''),
('产品恢复', 'product:recover', 1, ''),
('轮播管理', 'banner:list', 0, '/admin/banner'),
('轮播新增', 'banner:add', 6, ''),
('轮播编辑', 'banner:update', 6, ''),
('轮播删除', 'banner:delete', 6, ''),
('申请查看', 'apply:list', 0, '/admin/apply'),
('申请删除', 'apply:delete', 10, ''),
('申请恢复', 'apply:recover', 10, '');

-- 3. 测试用户（密码均为 123456 的 BCrypt 加密值）
-- 管理员：admin / 123456
INSERT INTO sys_user (phone, password, nick_name, role_id) VALUES
('admin', '$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG', '管理员', 1);

-- 运营：operator / 123456
INSERT INTO sys_user (phone, password, nick_name, role_id) VALUES
('operator', '$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG', '运营小张', 2);

-- 普通用户：13800138000 / 123456
INSERT INTO sys_user (phone, password, nick_name, role_id) VALUES
('13800138000', '$2b$10$Prsp.2y4ffOJVHrUtqbEbupKl2VnboOUjc3SxW81Z976vOLJzrHHG', '测试用户', 3);

-- 4. 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 5. 产品测试数据
INSERT INTO bank_product (product_name, product_type, rate, intro, img_path, sort) VALUES
-- 个人业务 (type=1)
('幸福存单', 1, 2.75, '个人大额存单，起存金额低，利率上浮，支持提前支取。', 'card/card01.png', 1),
('灵活理财', 1, 3.10, '随存随取，T+1到账，适合日常流动资金管理。', 'card/card02.png', 2),
('养老保障', 1, 3.50, '专为养老需求设计，长期稳健增值，可月缴。', 'card/card03.png', 3),
-- 信用卡 (type=2)
('标准信用卡', 2, 0.00, '终身免年费，消费累计积分，享各类商户优惠。', 'card/card04.png', 1),
('白金信用卡', 2, 0.00, '高额度，机场贵宾厅，高尔夫畅打，专属客服。', 'card/card05.png', 2),
('车主信用卡', 2, 0.00, '加油返现，洗车优惠，ETC自动扣款便捷出行。', 'card/card06.png', 3),
-- 公司金融 (type=3)
('企业贷', 3, 4.35, '小微企业专属贷款，纯信用无抵押，快速放款。', 'card/card07.png', 1),
('供应链金融', 3, 3.85, '依托核心企业信用，为上下游供应商提供融资。', 'card/card08.png', 2),
('单位结算卡', 3, 0.00, '企业账户结算，支持多级授权，资金安全管理。', 'card/card09.png', 3),
-- 普惠金融 (type=4)
('助农贷', 4, 4.00, '面向农户的优惠贷款，政府贴息，手续简便。', 'card/card10.png', 1),
('创业贷', 4, 4.50, '支持个人创业，最高额度50万，期限最长3年。', 'card/card11.png', 2),
('教育分期', 4, 0.00, '教育培训费用分期，0首付，低手续费，轻松学习。', 'card/card12.png', 3);

-- 6. 轮播测试数据
INSERT INTO banner (img_path, jump_route, sort) VALUES
('banner/banner01.png', '/personal', 1),
('banner/banner02.png', '/credit', 2),
('banner/banner03.png', '/company', 3);

-- 7. 申请测试数据
INSERT INTO apply_info (user_id, product_id, real_name, phone, apply_type) VALUES
(3, 1, '测试用户', '13800138000', 2),
(3, 4, '测试用户', '13800138000', 1);
