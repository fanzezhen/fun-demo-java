const DateUtil = {
    initVar: function (date) {
        const dateTime = new Date(date)
        const YY = dateTime.getFullYear()
        const MM =
            dateTime.getMonth() + 1 < 10
                ? '0' + (dateTime.getMonth() + 1)
                : dateTime.getMonth() + 1
        const DD =
            dateTime.getDate() < 10 ? '0' + dateTime.getDate() : dateTime.getDate()
        const hh =
            dateTime.getHours() < 10
                ? '0' + dateTime.getHours()
                : dateTime.getHours()
        const mm =
            dateTime.getMinutes() < 10
                ? '0' + dateTime.getMinutes()
                : dateTime.getMinutes()
        const ss =
            dateTime.getSeconds() < 10
                ? '0' + dateTime.getSeconds()
                : dateTime.getSeconds()
        return {dateTime: dateTime, YY: YY, MM: MM, DD: DD, hh: hh, mm: mm, ss: ss}
    },
    /**
     * 处理java.time.LocalDateTime类型的数据
     * @param date
     * @returns {{dateTime: Date, YY: number, MM: string, DD: string, hh: string, mm: string, ss: string}}
     */
    initLocalDateVar: function (date) {
        return {dateTime: date, YY: date[0], MM: date[1], DD: date[2], hh: date[3], mm: date[4], ss: date[5]}
    },
    // 格式化date
    formatDate: function (date, isLocal) {
        if (date == null) return ''
        const varMap = isLocal ? this.initLocalDateVar(date) : this.initVar(date)
        return `${varMap.YY}-${varMap.MM}-${varMap.DD}`
    },
    // 格式化datetime
    formatDateTime: function (datetime, isLocal) {
        if (datetime == null) return ''
        const varMap = isLocal ? this.initLocalDateVar(datetime) : this.initVar(datetime)
        return `${varMap.YY}-${varMap.MM}-${varMap.DD} ${varMap.hh}:${varMap.mm}:${varMap.ss}`
    }
}
