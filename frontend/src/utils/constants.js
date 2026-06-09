/**
 * 项目全局常量
 */

// API 基础地址（通过环境变量配置，默认使用 Vite 代理）
export const API_BASE_URL = import.meta.env.VITE_API_BASE || ''

// 图片资源基础地址（通过 Vite 代理指向后端）
export const IMAGE_BASE_URL = import.meta.env.VITE_IMAGE_BASE || '/bankImg/'

// 产品分类映射
export const PRODUCT_TYPE_MAP = {
  1: '个人业务',
  2: '信用卡',
  3: '公司金融',
  4: '普惠金融'
}

// 申请类型映射
export const APPLY_TYPE_MAP = {
  1: '办卡',
  2: '贷款'
}

// 角色映射
export const ROLE_MAP = {
  1: '超级管理员',
  2: '运营人员',
  3: '普通用户'
}

// 分页默认值
export const PAGE_SIZE = 10
