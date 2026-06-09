/**
 * 通用工具函数
 */

/**
 * 格式化日期字符串
 * @param {string|Date} date
 * @param {string} fmt 格式模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string}
 */
export function formatDate(date, fmt = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  const d = new Date(date)
  const pad = (n) => String(n).padStart(2, '0')
  return fmt
    .replace('YYYY', d.getFullYear())
    .replace('MM', pad(d.getMonth() + 1))
    .replace('DD', pad(d.getDate()))
    .replace('HH', pad(d.getHours()))
    .replace('mm', pad(d.getMinutes()))
    .replace('ss', pad(d.getSeconds()))
}

/**
 * 获取产品分类名称
 * @param {number} type
 * @returns {string}
 */
export function getProductTypeName(type) {
  const map = { 1: '个人业务', 2: '信用卡', 3: '公司金融', 4: '普惠金融' }
  return map[type] || '未知'
}

/**
 * 获取申请类型名称
 * @param {number} type
 * @returns {string}
 */
export function getApplyTypeName(type) {
  const map = { 1: '办卡', 2: '贷款' }
  return map[type] || '未知'
}
