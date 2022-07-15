const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // default interface address
        ws: true,
        changeOrigin: true,
        pathRewrite: {'^/api' : ''}
      }
    }
  }
})
