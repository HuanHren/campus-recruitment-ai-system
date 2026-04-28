import { demoApplications } from './applications'
import { demoCompanies } from './companies'
import { demoInterviews } from './interviews'
import { demoJobs } from './jobs'
import { demoStudents } from './students'

type CountMap = Record<string, number>

const TODAY = '2026-04-28'
const LAST_7_DAYS = ['2026-04-22', '2026-04-23', '2026-04-24', '2026-04-25', '2026-04-26', '2026-04-27', '2026-04-28']

function countBy<T extends Record<string, any>>(rows: T[], key: keyof T): CountMap {
  return rows.reduce((map, row) => {
    const value = String(row[key] || '未分类')
    map[value] = (map[value] || 0) + 1
    return map
  }, {} as CountMap)
}

function toChartData(map: CountMap) {
  return Object.entries(map).map(([name, value]) => ({ name, value }))
}

function percent(value: number, total: number) {
  if (!total) return 0
  return Math.round((value / total) * 100)
}

const applyStatusMap = countBy(demoApplications, 'applyStatusText')
const jobTypeMap = countBy(demoJobs, 'jobType')
const jobLevelMap = countBy(demoJobs, 'jobLevel')
const cityMap = countBy(demoJobs, 'city')
const todayApplications = demoApplications.filter(item => item.createdAt?.startsWith(TODAY))
const pendingCompanies = demoCompanies.filter(item => item.auditStatus === 'PENDING')
const pendingJobs = demoJobs.filter(item => item.auditStatus === 'PENDING')
const approvedCompanies = demoCompanies.filter(item => item.auditStatus === 'APPROVED')
const onlineJobs = demoJobs.filter(item => item.publishStatus === 'ONLINE')

export const dataOverview = {
  companies: demoCompanies.length,
  students: demoStudents.length,
  jobs: demoJobs.length,
  resumes: demoStudents.length,
  applications: demoApplications.length,
  interviews: demoInterviews.length,
  todayApplications: todayApplications.length,
  pendingCompanies: pendingCompanies.length,
  pendingJobs: pendingJobs.length,
  approvedCompanies: approvedCompanies.length,
  onlineJobs: onlineJobs.length,
  studentInterns: demoStudents.filter(item => item.grade.includes('实习')).length,
  studentGraduates: demoStudents.filter(item => item.grade.includes('应届')).length
}

export const adminMetrics = [
  {
    label: '注册学生数',
    value: dataOverview.students,
    trend: `${dataOverview.studentGraduates} 名应届`,
    tone: 'blue',
    icon: 'solar:user-id-bold-duotone',
    desc: `${dataOverview.studentInterns} 名实习生，学生画像更贴近校园招聘`
  },
  {
    label: '入驻企业数',
    value: dataOverview.companies,
    trend: `${dataOverview.approvedCompanies} 家通过`,
    tone: 'purple',
    icon: 'solar:buildings-2-bold-duotone',
    desc: '文字 Logo 演示数据，避免使用企业官方素材'
  },
  {
    label: '发布岗位数',
    value: dataOverview.jobs,
    trend: `${dataOverview.onlineJobs} 个在线`,
    tone: 'cyan',
    icon: 'solar:case-round-bold-duotone',
    desc: `实习、校招、管培生岗位共 ${dataOverview.jobs} 条`
  },
  {
    label: '今日投递数',
    value: dataOverview.todayApplications,
    trend: '实时联动',
    tone: 'green',
    icon: 'solar:paper-plane-bold-duotone',
    desc: `投递记录总量 ${dataOverview.applications} 条，状态分布均衡`
  },
  {
    label: '待审核企业',
    value: dataOverview.pendingCompanies,
    trend: dataOverview.pendingCompanies ? '需处理' : '已清零',
    tone: 'orange',
    icon: 'solar:shield-check-bold-duotone',
    desc: '等待认证资料、联系人和招聘说明核验'
  },
  {
    label: '待审核岗位',
    value: dataOverview.pendingJobs,
    trend: dataOverview.pendingJobs ? '需处理' : '已清零',
    tone: 'orange',
    icon: 'solar:document-add-bold-duotone',
    desc: '重点关注薪资范围、岗位级别和技能要求'
  }
]

export const adminTrend = LAST_7_DAYS.map(day => ({
  label: day.slice(5),
  value: demoApplications.filter(item => item.createdAt?.startsWith(day)).length
}))

export const jobCategory = toChartData(jobTypeMap)

export const jobLevelDistribution = toChartData(jobLevelMap)

export const cityDistribution = toChartData(cityMap).sort((a, b) => b.value - a.value)

export const applyStatusDistribution = toChartData(applyStatusMap)

export const auditProgress = [
  { label: '企业认证通过', value: demoCompanies.filter(item => item.auditStatus === 'APPROVED').length },
  { label: '企业待审核', value: dataOverview.pendingCompanies },
  { label: '岗位审核通过', value: demoJobs.filter(item => item.auditStatus === 'APPROVED').length },
  { label: '岗位待审核', value: dataOverview.pendingJobs }
]

export const adminTodos = [
  {
    title: '企业认证审核',
    desc: `${dataOverview.pendingCompanies} 家企业等待核验，建议优先检查联系人、邮箱和校招说明。`,
    level: '高'
  },
  {
    title: '岗位发布审核',
    desc: `${dataOverview.pendingJobs} 条岗位待审核，重点确认是否属于实习、应届、初级或管培生岗位。`,
    level: '中'
  },
  {
    title: '投递状态跟进',
    desc: `当前投递状态覆盖已投递、已查看、邀请面试、已录用、已拒绝，便于展示完整流程。`,
    level: '中'
  },
  {
    title: '低匹配度提醒',
    desc: 'AI 匹配分覆盖 47%-96%，可在答辩中展示高低匹配差异和简历优化价值。',
    level: '低'
  }
]

export const aiStatus = [
  { label: 'DeepSeek-V4-Pro 状态', value: '可演示', detail: '无 Key 自动切换丰富模拟数据' },
  { label: '今日 AI 调用次数', value: 126, detail: '包含简历优化、岗位匹配和面试题生成' },
  { label: '简历优化次数', value: 72, detail: '围绕应届生项目经历和技能关键词优化' },
  { label: '岗位匹配次数', value: 54, detail: '匹配分覆盖 47%-96%，便于展示差异' }
]

export const studentSummary = [
  { label: '简历完整度', value: '87%', desc: '项目经历、课程设计和实习意向较完整' },
  { label: 'AI 推荐岗位', value: demoJobs.filter(item => item.auditStatus === 'APPROVED' && item.publishStatus === 'ONLINE').length, desc: '优先推荐实习、校招和初级岗位' },
  { label: '待处理面试邀请', value: demoInterviews.filter(item => item.invitationStatus === 'PENDING').length, desc: '建议今日完成确认或改期沟通' }
]

export const applyTimeline = demoApplications.slice(0, 8).map(item => ({
  title: `${item.applyStatusText}：${item.companyName} ${item.jobName}`,
  time: item.createdAt.slice(5, 16),
  status: item.applyStatus === 'REJECTED' ? 'danger' : item.applyStatus === 'INTERVIEW' || item.applyStatus === 'OFFER' ? 'success' : 'primary'
}))

const companyJobCount = demoCompanies.map(company => ({
  ...company,
  jobs: demoJobs.filter(job => job.companyName === company.companyName).length
}))

export const hotCompanies = companyJobCount
  .sort((a, b) => b.jobs - a.jobs)
  .slice(0, 10)
  .map(item => ({
    name: item.companyName,
    field: `${item.industry} / ${item.city}`,
    jobs: item.jobs,
    city: item.city,
    scale: item.companyScale
  }))

export const companyMetrics = [
  { label: '发布中岗位', value: dataOverview.onlineJobs, trend: `${jobTypeMap['实习'] || 0} 个实习岗` },
  { label: '收到简历数', value: dataOverview.applications, trend: `${dataOverview.todayApplications} 条今日新增` },
  { label: '待处理投递', value: demoApplications.filter(item => ['PENDING', 'VIEWED'].includes(item.applyStatus)).length, trend: '需筛选' },
  { label: '面试安排', value: dataOverview.interviews, trend: '本月安排' }
]

export const candidates = demoApplications
  .slice(0, 10)
  .map(item => ({
    name: item.studentName,
    role: item.jobName,
    school: demoStudents.find(student => student.userId === item.studentUserId)?.school || '校园招聘候选人',
    match: item.aiMatchScore,
    status: item.applyStatusText
  }))

export const companyTrend = LAST_7_DAYS.map(day => ({
  label: day.slice(5),
  value: demoApplications.filter(item => item.createdAt?.startsWith(day)).length
}))

export const hotJobRanking = [...demoJobs]
  .sort((a, b) => b.aiHeat - a.aiHeat)
  .slice(0, 10)
  .map(job => ({ name: job.jobName, company: job.companyName, value: job.aiHeat }))

export const recruitmentConversion = [
  { label: '岗位发布', value: dataOverview.jobs, percent: 100 },
  { label: '简历投递', value: dataOverview.applications, percent: 86 },
  { label: '企业已查看', value: demoApplications.filter(item => ['VIEWED', 'INTERVIEW', 'OFFER', 'REJECTED'].includes(item.applyStatus)).length, percent: 72 },
  { label: '面试邀请', value: dataOverview.interviews, percent: percent(dataOverview.interviews, dataOverview.applications) },
  { label: '录用推进', value: demoApplications.filter(item => item.applyStatus === 'OFFER').length, percent: percent(demoApplications.filter(item => item.applyStatus === 'OFFER').length, dataOverview.applications) }
]
