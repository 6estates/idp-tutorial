<template>
  <div id="app">
    <!-- response -->
    <template v-if="hasResponse">
      <div class="top-op">
        <span class="btn-back" @click="handleBack">Go Back</span>
      </div>

      <div class="main">
        <div class="left">
          <iframe v-if="fileExtension === 'pdf'" :src="pdfUrl" />
          <PreviewPhoto v-else :src="pdfUrl" alt="" />
        </div>

        <div class="right">
          <div class="table-wrap">
            <!-- normal fields -->
            <div v-for="item in normalFields" :key="item.field_name">
              <div class="table-title">
                <span class="text">{{item.field_name}}</span>
              </div>

              <div class="table-value">
                <span class="text">{{item.value}}</span>
              </div>
            </div>

            <!-- table fields -->
            <div v-for="(item, index) in tableFields" :key="item.field_name">
              <div class="table-title">
                <span class="text">{{item.field_name}}</span>
              </div>

              <div class="table-value">
                <template v-if="item.tables.length">
                  <!-- for each page -->
                  <div v-for="pageItem in item.tables" :key="pageItem.page">
                    <div class="page-item">
                      <div class="title-wrap" @click="handleCollapsePage(index, pageItem.page)">
                        <span class="text">Page {{pageItem.page}}</span>
                        <i :class="[collapsePage[`${index}-${pageItem.page}`] ? 'el-icon-caret-top' : 'el-icon-caret-bottom']"></i>
                      </div>
                    </div>
                    
                    <!-- each page table -->
                    <div class="page-item-wrap" v-show="collapsePage[`${index}-${pageItem.page}`]">
                      <table class="page-table">
                        <tr v-for="(row, rowIndex) in pageItem.cell_objs" :key="rowIndex">
                          <template v-if='!item.template || !item.template.length'>
                            <td v-for="(cell, colIndex) in row" :key="colIndex">
                              <span>{{cell.text}}</span>
                            </td>
                          </template>

                          <template v-else>
                            <td v-for="(cell, colIndex) in row.slice(0, item.template.length)" :key="colIndex">
                              <span>{{cell.text}}</span>
                            </td>
                          </template>
                        </tr>
                      </table>
                    </div>
                  </div>
                </template>
                <span v-else>No Data</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- form -->
    <div class="form-wrap" v-else>
      <div class="form-title">{{title}}</div>
      <div class="form-box">
        <el-form
          class="form-list"
          :model="formData"
          :rules="rules"
          ref="form"
        >
          <el-form-item class="form-item" label="" prop="file">
            <div v-if="formData.file" class="file-item">
              <span class="file-name">{{formData.file.name}}</span>
              <i class="el-icon-error icon" @click="handleDeleteFile" />
            </div>
            <el-upload
              v-else
              class="upload-box"
              drag
              action=""
              accept=".pdf,.png,.jpeg,.jpg"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
            >
              <div class="pre-title">Supports: PDF, JPG, PNG</div>
              <div class="upload-title">Drop File Here</div>
              <div class="upload-sub-title">or use the button</div>
              <div class="upload-btn">
                <img :src="require('@/assets/upload.png')" alt="">
              </div>
            </el-upload>
          </el-form-item>
  
          <el-form-item class="form-item" label="" prop="fileType">
            <el-select
              class="content"
              v-model="formData.fileType"
              size="medium"
              placeholder="File Type"
            >
              <el-option
                v-for="item in sortFileTypes"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>        
  
          <el-form-item class="form-item" label="" prop="token">
            <el-input
              class="content"
              v-model.trim="formData.token"
              size="medium"
              placeholder="Access Token"
            />
          </el-form-item>
  
          <el-form-item class="form-item" label="" prop="mode">
            <el-select
              class="content"
              v-model="formData.mode"
              size="medium"
              placeholder="Mode"
            >
              <el-option value="1" label="Loop" />
              <el-option value="2" label="Callback" />
            </el-select>
          </el-form-item>
  
          <template v-if="formData.mode === '2'">
            <el-form-item
              class="form-item"
              label=""
              prop="callback"
              required
            >
              <el-input
                class="content"
                v-model.trim="formData.callback"
                size="medium"
                placeholder="Callback Link"
              />
            </el-form-item>
  
            <el-form-item
              class="form-item"
              label=""
              prop="callbackMode"
              required
            >
              <el-select
                class="content"
                v-model="formData.callbackMode"
                size="medium"
                placeholder="Callback Mode"
              >
                <el-option value="0" label="0" />
                <el-option value="1" label="1" />
                <el-option value="2" label="2" />
              </el-select>
            </el-form-item>
          </template>
        </el-form>
  
        <div class="btn-op" @click="handleSubmit">Submit</div>
      </div>
    </div>
    <div class="footer">
      <span>© 2022 </span>
      <a target="_blank" href="https://www.6estates.com">6Estates</a>
      <span> All Rights Reserved</span>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import PreviewPhoto from './components/previewPhoto'

export default {
  name: 'App',
  components: {
    PreviewPhoto
  },
  data() {
    return {
      hasResponse: false,
      loading: null,
      pdfUrl: '',
      title: 'Processing Your Documents Intelligently',
      fileExtension: '',
      formData: {
        token: '',
        mode: '',
        fileType: '',
        callback: '',
        callbackMode: '',
        file: null
      },
      resultFields: [],
      collapsePage: {},
      fileTypeList: [
        {
          label: 'Bank Statement',
          value: 'CBKS'
        },
        {
          label: 'Invoice',
          value: 'CINV'
        },
        {
          label: 'Cheque',
          value: 'CHQ'
        },
        {
          label: 'Credit Bureau Singapore',
          value: 'CBS'
        },
        {
          label: 'Receipt',
          value: 'RCPT'
        },
        {
          label: 'Payslip',
          value: 'PS'
        },
        {
          label: 'Packing List',
          value: 'PL'
        },
        {
          label: 'Bill of Lading',
          value: 'BL'
        },
        {
          label: 'Air Waybill',
          value: 'AWBL'
        },
        {
          label: 'Kartu Tanda Penduduk',
          value: 'KTP'
        },
        {
          label: 'Hong Kong Annual Return',
          value: 'HKAR'
        },
        {
          label: 'Purchase Order',
          value: 'PO'
        },
        {
          label: 'Delivery Order',
          value: 'DO'
        },
        {
          label: 'Singapore NRIC',
          value: 'NRIC'
        },
        {
          label: 'China ID Card',
          value: 'ZHID'
        },
        {
          label: 'China Passport',
          value: 'PP'
        },
        {
          label: 'ACRA Bizfile',
          value: 'BFP'
        },
        {
          label: 'Nomor Pokok Wajib Pajak',
          value: 'NPWP'
        }
      ],
      rules: {
        token: [
          { required: true, message: 'token is required.', trigger: 'change' },
        ],
        mode: [
          { required: true, message: 'mode is required.', trigger: 'blur' },
        ],
        callback: [
          {
            validator: (rule, value, callback) => {
              if (this.formData.mode === '2') {
                if (!value) {
                  callback(new Error('callback is required.'))
                } else {
                  callback()
                }
              } else {
                callback()
              }
            },
            trigger: 'blur'
          },
        ],
        callbackMode: [
          {
            validator: (rule, value, callback) => {
              if (this.formData.mode === '2') {
                if (!value) {
                  callback(new Error('callbackMode is required.'))
                } else {
                  callback()
                }
              } else {
                callback()
              }
            },
            trigger: 'blur'
          },
        ],
        fileType: [
          { required: true, message: 'fileType is required.', trigger: 'change' },
        ],
        file: [
          { required: true, message: 'file is required.', trigger: 'change' },
        ],
      }
    }
  },
  computed: {
    tableFields() {
      return this.resultFields.filter(item => item.type === 'table').map(item => {
        const value = item.value || {}
        const tables = value.tables || []
        return {
          ...item,
          tables,
          value: {
            tables
          }
        }
      })
    },
    normalFields() {
      return this.resultFields.filter(item => item.type !== 'table')
    },
    sortFileTypes() {
      const list = [...this.fileTypeList]
      return list.sort((a, b) => {
        return a.label.localeCompare(b.label)
      })
    }
  },
  created() {
    const name = this.getQueryVariable('name')
    if (name) this.title = decodeURIComponent(name)
  },
  methods: {
    handleFileChange(file) {
      this.$set(this.formData, 'file', file.raw)
      this.fileExtension = file.name.split('.').pop().toLowerCase()
    },
    handleDeleteFile() {
      this.formData.file = null
    },
    async handleSubmit() {
      const form = new FormData()
      try {
        await this.$refs.form.validate()
        Object.keys(this.formData).forEach(key => {
          form.append(key, this.formData[key])
        })
      } catch(e) {
        return
      }

      try {
        this.loading = this.$loading({
          text: 'Please be patient...'
        })
        const { data } = await axios.post('/api/taskSubmit', form)
        const message = data.message

        if (data.status === 200) {
          if (this.formData.mode === '1') {
            this.getTaskResult({
              taskId: data.data,
              token: this.formData.token
            })
          } else {
            this.loading.close()
            this.$message.success({
              duration: 10000,
              message: 'Your file has been submitted successfully, please check the result later in your callback service!'
            })
          }
        } else {
          this.$message.error({
            duration: 10000,
            message
          })
          this.loading.close()
        }
      } catch(e) {
        this.$message.error('Network Error')
        this.loading.close()
      }
    },
    async getTaskResult({ taskId, token }) {
      try {
        const { data } = await axios.post('/api/request', {
          taskId,
          token
        })
        if (data.status === 200) {
          this.loading.close()
          this.resultFields = data.data.fields
          this.readBlobAsDataURL(this.formData.file, url => {
            this.pdfUrl = url
            this.hasResponse = true
          })
        } else {
          this.timer = setTimeout(() => {
            this.getTaskResult({ taskId, token })
          }, 3000)
        }
      } catch (err) {
        this.loading.close()
        this.$message.error(err.message)
      }
    },
    handleCollapsePage(index, pageIndex) {
      this.$set(
        this.collapsePage,
        `${index}-${pageIndex}`,
        !this.collapsePage[`${index}-${pageIndex}`]
      )
    },
    handleBack() {
      this.hasResponse = false
    },
    readBlobAsDataURL(blob, callback) {
      const a = new FileReader()
      a.onload = function(e) {callback(e.target.result)}
      a.readAsDataURL(blob)
    },
    getQueryVariable(variable) {
      const query = window.location.search.substring(1)
      const vars = query.split("&")
      for (let i = 0; i < vars.length; i++) {
        const pair = vars[i].split('=')
        if(pair[0] == variable){return pair[1]}
      }
      return(false);
    }
  }
}
</script>

<style lang="less">
body {
  margin: 0;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
.form-wrap {
  padding-bottom: 20px;
  padding-top: 1vh;
  background: url('~@/assets/bg.png') no-repeat center center;
  background-size: cover;
  min-height: calc(99vh - 50px);
}
.form-box {
  margin: 0 auto;
  padding: 30px;
  width: 400px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 3px 2px 10px rgba(0, 0, 0, 0.1);
}
.form-title {
  margin-top: 60px;
  font-size: 34px;
  text-align: center;
  color: #2eb5b1;
  font-weight: bold;
  padding-bottom: 30px;
}
.form-item {
  display: flex;
  margin-bottom: 15px;
  .el-form-item__content, .el-select  {
    width: 100%;
  }
}
.upload-box {
  .el-upload {
    width: 100%;
  }
  .el-upload-dragger {
    width: 100%;
    height: auto;
  }
}
.pre-title {
  padding-top: 20px;
  color: #19d8ca;
  font-size: 20px;
}
.upload-title {
  color: #19d8ca;
  margin-top: 10px;
  font-size: 30px;
}
.upload-sub-title {
  color: #c7c7c7;
  font-size: 18px;
}
.upload-btn {
  margin-top: 10px;
  img {
    height: 60px;
    cursor: pointer;
  }
}
.btn-upload {
  line-height: 30px;
  padding: 0 10px;
  border-radius: 4px;
  background-color: #2eb5b1;
  color: #fff;
}
.btn-op {
  margin-top: 30px;
  padding: 14px 0;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  background-color: #2eb5b1;
  color: #fff;
  font-weight: bold;
  font-size: 18px;
}
.file-item {
  padding: 0 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fafafa;
  border-radius: 5px;
  .file-name {
    overflow: hidden;
    text-overflow: ellipsis;
    color: #2eb5b1;
  }
  .icon {
    cursor: pointer;
    font-size: 18px;
    color: #aaa;
    &:hover {
      color: #2eb5b1;
    }
  }
}
.top-op {
  padding: 10px 20px;
  text-align: right;
}
.btn-back {
  padding: 5px 16px;
  border-radius: 4px;
  cursor: pointer;
  background-color: #6b6d71;
  color: #fff;
  margin-right: 40px;
}
.main {
  display: flex;
  justify-content: space-between;
  height: calc(100vh - 72px);
}
.left {
  width: 40%;
  height: 100%;
  overflow-y: auto;
  iframe {
    width: calc(100% - 10px);
    height: calc(100% - 10px);
  }
  img {
    width: 100%;
  }
}
.right {
  width: 59%;
  height: 100%;
  overflow-y: auto;
}
.table-wrap {
  font-family: Roboto;
  border: 1px solid #d0d0d0;
}
.circle {
  margin-left: 8px;
  width: 8px;
  height: 8px;
  border-radius: 100%;
}
.el-input__inner {
  line-height: 28px !important;
  height: 28px !important;
  min-width: 150px;
}
.empty {
  p {
    text-align: center;
    padding-top: 50px;
    font-size: 22px;
  }
}
.btn-icon {
  margin-left: 10px;
  font-size: 24px;
  cursor: pointer;
}
.table-title {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 12px 12px 8px;
  box-shadow: 2px 0 10px 0 rgba(0, 0, 0, 0.15);
  background-color: #f0f0f0;
  &:first-child {
    box-shadow: -1px 0px 7px 0px rgb(0 0 0 / 10%) inset;
  }
  & > .text {
    font-size: 22px;
    color: #2eb5b1;
  }
}
.table-value {
  padding: 12px;
  overflow-x: auto;
  & > .text {
    font-size: 16px;
    color: #4d4f53;
    line-height: 1.2;
  }
}
.page-item {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  &:first-child {
    margin-top: 0;
  }
  .title-wrap {
    display: flex;
    justify-content: space-between;
    width: 100%;
    padding: 8px 15px;
    box-shadow: 0px 2px 10px 0px rgba(0, 0, 0, 0.15);
    cursor: pointer;
  }
}
.page-item-wrap {
  overflow: auto;
  .page-table {
    font-size: 14px;
    width: 100%;
    tr:first-child td span {
      font-weight: bold;
      white-space: nowrap;
    }
    td {
      min-width: 100px;
      padding: 12px;
      color: #4d4f53;
      box-shadow: 0px 2px 6px 3px rgba(0, 0, 0, 0.05);
    }
    .td-relative {
      padding-top: 22px;
      position: relative;
    }
  }
}
.table-header-op {
  padding-bottom: 5px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
}
.trOpWrap {
  padding: 20px 5px;
  display: flex;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
}
.el-icon-close {
  position: absolute;
  right: 0;
  top: 5px;
  font-weight: bold;
  cursor: pointer;
}
.footer {
  height: 30px;
  line-height: 30px;
  text-align: center;
  font-size: 12px;
  font-weight: bold;
  color: #6b6d71;
  a {
    color: #2eb5b1;
  }
}
</style>
