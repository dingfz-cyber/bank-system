import { computed } from 'vue'
import { IMAGE_BASE_URL } from '@/utils/constants'

/**
 * 图片 URL 处理 composable
 * @param {string} path 相对路径，如 banner/banner01.png
 * @returns {{ imageUrl: import('vue').ComputedRef<string> }}
 *
 * @example
 * const { imageUrl } = useImage('card/card01.png')
 * // imageUrl.value => '/bankImg/card/card01.png'
 */
export function useImage(path) {
  const imageUrl = computed(() => {
    if (!path) return ''
    // 如果已经是完整 URL 则直接返回
    if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
      return path
    }
    return `${IMAGE_BASE_URL}${path}`
  })

  /**
   * 图片加载失败时的兜底处理
   * @param {Event} event
   */
  function handleImageError(event) {
    const target = event.target
    if (target) {
      target.src = ''  // 可替换为兜底图
      target.style.display = 'none'
    }
  }

  return {
    imageUrl,
    handleImageError
  }
}
