<template>
  <div class="page-header-index-wide" id="fullscreen">
    <a-button @click="getFullCreeen()" :icon="icon" size="small" type="primary">点我可以全屏或退出全屏</a-button>


    <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="ToQuestionList(item.id)">试题点评</a-button>
    <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toTotalScore(item.examName)">总成绩</a-button>
    <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toScoreList(item.id)">本场成绩</a-button>
    <a-button size="big" type= "primary" style=" float: right;margin-right: 1.0em;margin-top: 1.0em" @click="toExamList(item.id)">考生答卷</a-button>
  </div>
</template>

<script>

  export default {
    name: "Analysis",
    components: {
    },
    data() {
      return {
        n:0,
        icon: 'arrows-alt'
      }
    },
    created() {
    },
    methods: {
      getFullCreeen(){
            this.n++;
            if (this.n%2==0) {
              this.outFullCreeen(document)
              this.icon = 'arrows-alt'
            } else {
              this.inFullCreeen(document.getElementById('fullscreen'))
              this.icon = 'shrink'
            }
        },
        inFullCreeen(element){
            let el = element;
            let rfs = el.requestFullScreen || el.webkitRequestFullScreen ||
                el.mozRequestFullScreen || el.msRequestFullScreen;
            if (typeof rfs != "undefined" && rfs) {
                rfs.call(el);
            } else if (typeof window.ActiveXObject != "undefined") {
                let wscript = new ActiveXObject("WScript.Shell");
                if (wscript != null) {
                    wscript.SendKeys("{F11}");
                }
            }
        },
        outFullCreeen(element){
            let el = element;
            let cfs = el.cancelFullScreen || el.webkitCancelFullScreen ||
                el.mozCancelFullScreen || el.exitFullScreen;
            if (typeof cfs != "undefined" && cfs) {
                cfs.call(el);
            } else if (typeof window.ActiveXObject != "undefined") {
                let wscript = new ActiveXObject("WScript.Shell");
                if (wscript != null) {
                    wscript.SendKeys("{F11}");
                }
            }
        },
      ToQuestionList(examid) {
        this.$router.push({ name: 'exammanage-comment', params: {examid: examid}  })
      },
      toExamList (examid) {
        this.$router.push({ name: 'exam-submit', params: {examid: examid} })
      },
      toScoreList (examid) {
        this.$router.push({ name: 'exam-score', params: {examid: examid} })
      },
      toAnalysis (examid) {
        this.$router.push({ name: 'Analysis', params: {examid: examid} })
      },
      toTotalScore (examName) {
        this.$router.push({ name: 'exam-totalscore', params: {examName: examName} })
      },
    }
  }
</script>

<style lang="scss" scoped>
</style>