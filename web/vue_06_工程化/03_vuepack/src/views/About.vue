<template>
  <div>
    <el-button type="primary" @click="readFile('C:\study\javaStudy\web')">文件</el-button>
    <ul>
      <li>
        {{fileName}}
      </li>
    </ul>
  </div>
</template>
<script>
export default {
  data() {
    return {
      fileName: ''
    }
  },
  methods: {
    readFile(filePath) {
      // 创建一个新的xhr对象
      let xhr = null
      if (window.XMLHttpRequest) {
        xhr = new XMLHttpRequest()
      } else {
        // eslint-disable-next-line
        xhr = new ActiveXObject('Microsoft.XMLHTTP')
      }
      const okStatus = document.location.protocol === 'file' ? 0 : 200
      xhr.open('GET', filePath, false)
      xhr.overrideMimeType('text/html;charset=utf-8')
      xhr.send(null)
      list = xhr.responseText
      return xhr.status === okStatus ? xhr.responseText : null
    }
  }
}
</script>