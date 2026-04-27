export const adminMetrics = [
  { label: '注册学生数', value: 1286, trend: '+12.4%', tone: 'blue' },
  { label: '入驻企业数', value: 236, trend: '+8.1%', tone: 'purple' },
  { label: '发布岗位数', value: 518, trend: '+15.7%', tone: 'cyan' },
  { label: '今日投递数', value: 94, trend: '+21', tone: 'green' },
  { label: '待审核企业', value: 18, trend: '需处理', tone: 'orange' },
  { label: '待审核岗位', value: 27, trend: '需处理', tone: 'orange' }
]

export const adminTrend = [
  { label: '周一', value: 42 },
  { label: '周二', value: 58 },
  { label: '周三', value: 64 },
  { label: '周四', value: 73 },
  { label: '周五', value: 86 },
  { label: '周六', value: 71 },
  { label: '周日', value: 94 }
]

export const jobCategory = [
  { label: '技术研发', value: 42, color: '#2563EB' },
  { label: '产品运营', value: 24, color: '#7C3AED' },
  { label: '市场销售', value: 18, color: '#10B981' },
  { label: '职能支持', value: 16, color: '#F59E0B' }
]

export const auditProgress = [
  { label: '企业认证通过', percent: 78, value: '184 家' },
  { label: '岗位审核通过', percent: 84, value: '436 个' },
  { label: '资料待补充', percent: 16, value: '38 条' }
]

export const adminTodos = [
  { title: '企业认证审核', desc: '18 家企业等待营业执照与联系人信息核验', level: '高' },
  { title: '岗位发布审核', desc: '27 个岗位需要确认薪资、城市与任职要求', level: '中' },
  { title: '面试邀请记录', desc: '今日新增 36 条面试邀请，需关注学生确认情况', level: '中' }
]

export const aiStatus = [
  { label: 'DeepSeek-V4-Pro', value: '可演示', detail: '无 Key 自动切换模拟数据' },
  { label: '今日 AI 调用次数', value: 126, detail: '较昨日 +18' },
  { label: '简历优化次数', value: 68, detail: '学生端高频功能' },
  { label: '岗位匹配次数', value: 42, detail: '平均匹配度 82%' }
]

export const studentSummary = [
  { label: '简历完整度', value: '86%', desc: '建议补充项目量化成果' },
  { label: 'AI 推荐岗位', value: 18, desc: '基于技能标签与城市意向' },
  { label: '待处理面试邀请', value: 3, desc: '2 个线上面试，1 个现场面试' }
]

export const recommendedJobs = [
  {
    title: 'Java 后端开发工程师',
    company: '杭州云启科技有限公司',
    city: '杭州',
    salary: '10k-16k',
    tags: ['Spring Boot', 'MySQL', 'Redis'],
    match: 92
  },
  {
    title: 'AI 产品助理',
    company: '上海智聘数据科技',
    city: '上海',
    salary: '8k-12k',
    tags: ['需求分析', 'AIGC', '原型设计'],
    match: 86
  },
  {
    title: '前端开发实习生',
    company: '南京星图软件',
    city: '南京',
    salary: '6k-9k',
    tags: ['Vue 3', 'Element Plus', '可视化'],
    match: 81
  }
]

export const applyTimeline = [
  { title: '完善简历', time: '今日 09:20', status: 'success' },
  { title: 'AI 匹配岗位', time: '今日 10:05', status: 'success' },
  { title: '投递 Java 后端岗位', time: '今日 10:18', status: 'primary' },
  { title: '等待企业查看', time: '预计 24 小时内', status: 'warning' }
]

export const hotCompanies = [
  { name: '云启科技', field: '企业服务 / 后端研发', jobs: 12 },
  { name: '智聘数据', field: 'AI 招聘 / 产品运营', jobs: 8 },
  { name: '星图软件', field: '可视化 / 前端开发', jobs: 6 }
]

export const companyMetrics = [
  { label: '发布中岗位', value: 12, trend: '+3 本周' },
  { label: '收到简历数', value: 186, trend: '+34 今日' },
  { label: '待处理投递', value: 28, trend: '需筛选' },
  { label: '面试安排', value: 16, trend: '本周' }
]

export const candidates = [
  { name: '张同学', role: 'Java 后端开发', school: '示例大学', match: 91, status: '建议邀约' },
  { name: '李同学', role: '前端开发', school: '信息工程学院', match: 87, status: '待查看' },
  { name: '王同学', role: '产品助理', school: '管理学院', match: 82, status: '进入面试' }
]

export const companyTrend = [
  { label: '周一', value: 18 },
  { label: '周二', value: 24 },
  { label: '周三', value: 31 },
  { label: '周四', value: 28 },
  { label: '周五', value: 39 },
  { label: '周六', value: 26 },
  { label: '周日', value: 34 }
]

export const resumeAdviceTags = ['量化项目成果', '突出技术栈', '补充实习职责', '优化求职意向', '增强关键词匹配']

export const interviewQuestionTypes = [
  { type: '基础题', color: '#2563EB' },
  { type: '项目题', color: '#7C3AED' },
  { type: '综合题', color: '#10B981' }
]
