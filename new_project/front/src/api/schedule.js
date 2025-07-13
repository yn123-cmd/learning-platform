import api from './config'

// 课程安排管理API
export const scheduleAPI = {
  // 获取今日课程安排
  getTodaySchedules() {
    return api.get('/auth/real/schedules/today')
  },

  // 获取指定日期的课程安排
  getSchedulesByDate(date) {
    return api.get(`/auth/real/schedules/date/${date}`)
  },

  // 获取本周课程安排
  getWeekSchedules() {
    return api.get('/auth/real/schedules/week')
  },

  // 获取正在进行的课程
  getOngoingSchedules() {
    return api.get('/auth/real/schedules/ongoing')
  },

  // 获取即将开始的课程
  getUpcomingSchedules() {
    return api.get('/auth/real/schedules/upcoming')
  },

  // 更新课程状态
  updateScheduleStatus(scheduleId, status) {
    return api.put(`/auth/real/schedules/${scheduleId}/status?status=${status}`)
  },

  // 获取教师课程安排
  getTeacherSchedules(teacherId, date = null) {
    const params = date ? `?date=${date}` : ''
    return api.get(`/auth/real/schedules/teacher/${teacherId}${params}`)
  },

  // 获取班级课程安排
  getClassSchedules(classId, date = null) {
    const params = date ? `?date=${date}` : ''
    return api.get(`/auth/real/schedules/class/${classId}${params}`)
  }
}

export default scheduleAPI 