const { defineConfig } = require('@vue/cli-service')
const projectConfig = require('./project-config.json')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: projectConfig.frontendPort
  }
})
