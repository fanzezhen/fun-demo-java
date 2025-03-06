const LocalDateUtil = {
    initVar: function (date) {
        return {dateTime: date, YY: date[0], MM: date[1], DD: date[2], hh: date[3], mm: date[4], ss: date[5]}
    },
    // 格式化date
    formatDate: function (date) {
        if (date == null) return ''
        const varMap = this.initVar(date)
        return `${varMap.YY}-${varMap.MM}-${varMap.DD}`
    },
    // 格式化datetime
    formatDateTime: function (datetime) {
        if (datetime == null) return ''
        const varMap = this.initVar(datetime)
        return `${varMap.YY}-${varMap.MM}-${varMap.DD} ${varMap.hh}:${varMap.mm}:${varMap.ss}`
    }
}
