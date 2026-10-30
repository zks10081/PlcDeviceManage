<template>
  <div>
    <!-- 字典类型 -->
    <el-card header="字典类型">
      <el-form :inline="true" :model="typeQuery" @submit.prevent>
        <el-form-item label="类型编码">
          <el-input v-model="typeQuery.dictType" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="类型名称">
          <el-input v-model="typeQuery.dictName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadTypes">查询</el-button>
          <el-button :icon="Refresh" @click="resetTypeQuery">重置</el-button>
          <el-button v-if="authStore.isAdmin" type="success" :icon="Plus" @click="openTypeDialog()">新增类型</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="typeList" border stripe v-loading="typeLoading">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="dictType" label="类型编码" />
        <el-table-column prop="dictName" label="类型名称" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="160" v-if="authStore.isAdmin">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openTypeDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteType(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 16px; justify-content: flex-end"
        layout="total, prev, pager, next"
        :total="typeTotal"
        :page-size="typeQuery.size"
        :current-page="typeQuery.page"
        @current-change="(p) => { typeQuery.page = p; loadTypes() }"
      />
    </el-card>

    <!-- 字典数据 -->
    <el-card header="字典数据" style="margin-top: 16px">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="字典类型">
          <el-select v-model="currentType" placeholder="请选择字典类型" filterable @change="loadDataList" style="width: 240px">
            <el-option v-for="o in dictOptions" :key="o.dictType" :label="o.dictName + '(' + o.dictType + ')'" :value="o.dictType" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button v-if="authStore.isAdmin" type="success" :icon="Plus" :disabled="!currentType" @click="openDataDialog()">新增数据</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="dataList" border stripe v-loading="dataLoading">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="label" label="标签" />
        <el-table-column prop="value" label="值" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="160" v-if="authStore.isAdmin">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openDataDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 16px; justify-content: flex-end"
        layout="total, prev, pager, next"
        :total="dataTotal"
        :page-size="dataQuery.size"
        :current-page="dataQuery.page"
        @current-change="(p) => { dataQuery.page = p; loadDataList() }"
      />
    </el-card>

    <!-- 类型弹窗 -->
    <el-dialog v-model="typeVisible" :title="typeForm.id ? '编辑类型' : '新增类型'" width="460px">
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="90px">
        <el-form-item label="类型编码" prop="dictType">
          <el-input v-model="typeForm.dictType" :disabled="!!typeForm.id" />
        </el-form-item>
        <el-form-item label="类型名称" prop="dictName">
          <el-input v-model="typeForm.dictName" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="typeForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeVisible = false">取消</el-button>
        <el-button type="primary" :loading="typeSubmitting" @click="submitType">确定</el-button>
      </template>
    </el-dialog>

    <!-- 数据弹窗 -->
    <el-dialog v-model="dataVisible" :title="dataForm.id ? '编辑数据' : '新增数据'" width="460px">
      <el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="90px">
        <el-form-item label="字典类型">
          <el-input :model-value="currentType" disabled />
        </el-form-item>
        <el-form-item label="标签" prop="label">
          <el-input v-model="dataForm.label" />
        </el-form-item>
        <el-form-item label="值" prop="value">
          <el-input v-model="dataForm.value" :disabled="!!dataForm.id" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dataForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dataForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataVisible = false">取消</el-button>
        <el-button type="primary" :loading="dataSubmitting" @click="submitData">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getDictPage, createDict, updateDict, deleteDict,
  getDictOptions, getDictDataPage, createDictData, updateDictData, deleteDictData
} from '@/api/dict'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()

/* 字典类型 */
const typeLoading = ref(false)
const typeList = ref([])
const typeTotal = ref(0)
const typeQuery = reactive({ dictType: '', dictName: '', page: 1, size: 10 })
const typeVisible = ref(false)
const typeSubmitting = ref(false)
const typeFormRef = ref()
const typeForm = reactive({ id: null, dictType: '', dictName: '', remark: '' })
const typeRules = {
  dictType: [{ required: true, message: '请输入类型编码', trigger: 'blur' }],
  dictName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }]
}

const loadTypes = () => {
  typeLoading.value = true
  getDictPage(typeQuery)
    .then((res) => { typeList.value = res.data.list; typeTotal.value = res.data.total })
    .finally(() => { typeLoading.value = false })
}
const resetTypeQuery = () => { typeQuery.dictType = ''; typeQuery.dictName = ''; typeQuery.page = 1; loadTypes() }
const openTypeDialog = (row) => {
  if (row) Object.assign(typeForm, { id: row.id, dictType: row.dictType, dictName: row.dictName, remark: row.remark })
  else Object.assign(typeForm, { id: null, dictType: '', dictName: '', remark: '' })
  typeVisible.value = true
}
const submitType = () => {
  typeFormRef.value.validate((valid) => {
    if (!valid) return
    typeSubmitting.value = true
    const api = typeForm.id ? updateDict : createDict
    api({ ...typeForm })
      .then(() => { ElMessage.success('保存成功'); typeVisible.value = false; loadTypes(); loadOptions() })
      .finally(() => { typeSubmitting.value = false })
  })
}
const deleteType = (row) => {
  ElMessageBox.confirm(`确认删除字典类型「${row.dictName}」？`, '提示', { type: 'warning' })
    .then(() => deleteDict(row.id))
    .then(() => { ElMessage.success('删除成功'); loadTypes(); loadOptions() })
    .catch(() => {})
}

/* 字典数据 */
const dictOptions = ref([])
const currentType = ref('')
const dataLoading = ref(false)
const dataList = ref([])
const dataTotal = ref(0)
const dataQuery = reactive({ dictType: '', label: '', page: 1, size: 10 })
const dataVisible = ref(false)
const dataSubmitting = ref(false)
const dataFormRef = ref()
const dataForm = reactive({ id: null, dictType: '', label: '', value: '', sort: 0, remark: '' })
const dataRules = {
  label: [{ required: true, message: '请输入标签', trigger: 'blur' }],
  value: [{ required: true, message: '请输入值', trigger: 'blur' }]
}

const loadOptions = () => getDictOptions().then((res) => {
  dictOptions.value = res.data
  if (!currentType.value && res.data.length) {
    currentType.value = res.data[0].dictType
    loadDataList()
  }
})
const loadDataList = () => {
  if (!currentType.value) { dataList.value = []; dataTotal.value = 0; return }
  dataQuery.dictType = currentType.value
  dataLoading.value = true
  getDictDataPage(dataQuery)
    .then((res) => { dataList.value = res.data.list; dataTotal.value = res.data.total })
    .finally(() => { dataLoading.value = false })
}
const openDataDialog = (row) => {
  if (row) Object.assign(dataForm, { id: row.id, dictType: currentType.value, label: row.label, value: row.value, sort: row.sort, remark: row.remark })
  else Object.assign(dataForm, { id: null, dictType: currentType.value, label: '', value: '', sort: 0, remark: '' })
  dataVisible.value = true
}
const submitData = () => {
  dataFormRef.value.validate((valid) => {
    if (!valid) return
    dataForm.dictType = currentType.value
    dataSubmitting.value = true
    const api = dataForm.id ? updateDictData : createDictData
    api({ ...dataForm })
      .then(() => { ElMessage.success('保存成功'); dataVisible.value = false; loadDataList() })
      .finally(() => { dataSubmitting.value = false })
  })
}
const deleteData = (row) => {
  ElMessageBox.confirm(`确认删除字典数据「${row.label}」？`, '提示', { type: 'warning' })
    .then(() => deleteDictData(row.id))
    .then(() => { ElMessage.success('删除成功'); loadDataList() })
    .catch(() => {})
}

onMounted(() => {
  loadTypes()
  loadOptions()
})
</script>
