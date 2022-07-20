<template>
  <div class="wrap">
    <div class="img-wrap">
      <img ref="attachment" draggable="false" :src="src" @mousedown="handleMove"/>
    </div>
    <div class="img-tool">
      <i class="el-icon-zoom-in icon" @click="handleScaleUp"></i>
      <i class="el-icon-zoom-out icon" @click="handleScaleDown"></i>
      <i class="el-icon-refresh-left icon" @click="handleRotateLeft"></i>
      <i class="el-icon-refresh-right icon" @click="handleRotateRight"></i>
    </div>
  </div>
</template>

<script>

export default {
  name: 'PreviewPhoto',
  props: {
    src: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      options: {
        scale: 1,
        rotate: 0
      }
    }
  },
  methods: {
    handleScaleUp() {
      this.options.scale += 0.5
      const baseStyle = this.$refs.attachment.style
      const baseTransform = baseStyle.transform.replace(/scale\(\d+(\.\d+)?\)/g, '')
      baseStyle.transform = baseTransform + `scale(${this.options.scale})`
    },
    handleScaleDown() {
      if (this.options.scale <= 0.5) return
      this.options.scale -= 0.5
      const baseStyle = this.$refs.attachment.style
      const baseTransform = baseStyle.transform.replace(/scale\(\d+(\.\d+)?\)/g, '')
      baseStyle.transform = baseTransform + `scale(${this.options.scale})`
    },
    handleRotateLeft() {
      this.options.rotate -= 90
      const baseStyle = this.$refs.attachment.style
      const baseTransform = baseStyle.transform.replace(/rotate\(-?\d+deg\)/g, '')
      baseStyle.transform = baseTransform + `rotate(${this.options.rotate}deg)`
    },
    handleRotateRight() {
      this.options.rotate += 90
      const baseStyle = this.$refs.attachment.style
      const baseTransform = baseStyle.transform.replace(/rotate\(-?\d+deg\)/g, '')
      baseStyle.transform = baseTransform + `rotate(${this.options.rotate}deg)`
    },
    handleMove(e) {
      const baseStyle = this.$refs.attachment.style
      const preTransform = baseStyle.transform
      const moveArr = preTransform.match(/translate\((-?\d+)px, (-?\d+)px\)/)

      // Calculate the position of the mouse relative element
      const disX = e.clientX
      const disY = e.clientY

      let preX = 0
      let preY = 0
      if (moveArr) {
        preX = Number(moveArr[1])
        preY = Number(moveArr[2])
      }
      // Events pressed and moved by the mouse
      document.onmousemove = (e) => {
        // The position of the element is obtained by subtracting the position of the mouse from the position of the mouse
        const left = (e.clientX - disX) / this.options.scale
        const top = (e.clientY - disY) / this.options.scale
        let baseTransform = preTransform.replace(/translate\(-?\d+(\.\d+)?px, -?\d+(\.\d+)?px\)/g, '')
        if (this.options.rotate !== 0) {
          baseTransform = baseTransform.replace(/rotate\(-?\d+deg\)/g, '')
          baseStyle.transform = baseTransform + `translate(${left + preX}px,${top + preY}px)` + `rotate(${this.options.rotate}deg)`
        } else {
          baseStyle.transform = baseTransform + `translate(${left + preX}px,${top + preY}px)`
        }
      }
      document.onmouseup = () => {
        document.onmousemove = null
        document.onmouseup = null
      }
    }
  }
}
</script>

<style lang="less" scoped>
.wrap {
  height: 100%;
}
.img-wrap {
  border: 1px solid #f5f5f5;
  text-align: center;
  overflow: hidden;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
  height: calc(100% - 52px);
  img {
    width: 100%;
    height: auto;
  }
}
.img-tool {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  background-color: #f0f0f0;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  .icon {
    font-size: 30px;
    margin: 0 5px;
    cursor: pointer;
  }
}
</style>
