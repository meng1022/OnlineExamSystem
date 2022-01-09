import React from 'react';

function ood() {
  return (new Date()).getTime() >= (new Date('2019/09/09 23:59:00')).getTime();
}

var href;
const data = {
  button: ood() ? '进入考试系统' : '注册', // 立即报名
  code: '支付宝扫码报名',
  url: 'https://render.alipay.com/p/w/seeconftwa/www/enrol.html',
  pre_url: 'https://render-pre.alipay.com/p/w/seeconftwa/www/enrol.html',
  schemeUrl: 'https://ds.alipay.com/?scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D20000067%26url%3Dhttps%253A%252F%252Frender.alipay.com%252Fp%252Fw%252Fseeconftwa%252Fwww%252Fenrol.html%253F__webview_options__%253D',
  pre_schemeUrl: 'https://ds.alipay.com/?scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FappId%3D20000067%26url%3Dhttps%253A%252F%252Frender-pre.alipay.com%252Fp%252Fw%252Fseeconftwa%252Fwww%252Fenrol.html%253F__webview_options__%253D',
  banner: {
    // enName: [
    //   <span key="1">National TB Clinical Diagnosis and Treatment Skills</span>,
    //   <span className="mobile-hide" key="2"> Competition</span>,
    // ],
    title: '2019全国结核病临床诊疗技能竞赛',
    title_m1: '2019全国结核病临床诊疗',
    title_m2: '技能竞赛',
    cnName_1: '指导：国家卫生健康委员会疾病预防控制局',
    cnName_2: '主办：中国疾控中心结核病防治临床中心',
    time: `${ood() ? '考试开始时间：2019-09-10 08:30' : '注册截止时间：2019-09-09 23:59'}，非常感谢您的关注`, // 2018.01.06  ／  中国  ·  杭州
  },
  page1: {
    title: '特邀专家',
    coming: '更多重量级大咖，敬请期待',
    row: [
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '专家A', post: 'xxx研究院' },
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '专家B', post: 'xxx研究中心' },
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '教授C', post: 'xxx医科大学' },
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '教授D', post: 'xxx医学院' },
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '博士E', post: 'xxx医药大学' },
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '专家F', post: 'xxx研究中心' },
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '专家G', post: 'xxx医院' },
      { src: '//qiniu.hkangcloud.com/yisheng.jpg', name: '教授H', post: 'xxx附属医院' },
    ],
  },
  page2: {
    title: '竞赛安排',
    timeline: [
      {
        src: 'http://qiniu.hkangcloud.com/yisheng.jpg',
        name: '',
        post: '',
        time: '09:00 - 09:15',
        title: '第一科考试',
      },
      {
        src: 'http://qiniu.hkangcloud.com/yisheng.jpg',
        name: '',
        post: '',
        time: '09:15 - 10:00',
        title: '第二科考试',
      },
      {
        src: 'http://qiniu.hkangcloud.com/yisheng.jpg',
        name: '',
        post: '',
        time: '10:00 - 10:45',
        title: '第三科考试',
       },
      {
        //double: true,
        src: 'http://qiniu.hkangcloud.com/yisheng.jpg',
        name: '',
        post: '',
        time: '10:45 - 11:30',
        title: '',
      },
      {
        src: 'http://qiniu.hkangcloud.com/yisheng.jpg',
        time: '11:30 - 13:00',
        title: '午间休息',
      },

    ],
  },
  page4: {
    title: '丰富的特色展台',
    content: '特色展台包括 Ant Design 、AntV、AntG、Egg 等明星产品，更有产品专家',
    content2: '现场问诊，为你答疑解难',
  },
  page3: {
    title: '现场竞赛地点',
    content: '北京市朝阳区德胜门外北沙滩亚奥国际酒店',
  },
  page5: {
    title: '探索 · 看见',
  },
  page6: {
        title: '友情链接',
    block: [
        '//qiniu.hkangcloud.com/1.jpg',
        '//qiniu.hkangcloud.com/2.jpg',
        '//qiniu.hkangcloud.com/3.jpg',
        '//qiniu.hkangcloud.com/IATB.jpg',
    ],
  },
  footer: {
    name: 'Copyright ©  中国疾控结核病防治临床中心',
    logoName: '2019全国结核病临床诊疗技能竞赛',
  },
};

export default data;

// image 预加载，避逸出不了图;
const div = document.createElement('div');
div.style.display = 'none';
document.body.appendChild(div);
function loadImage(src) {
  const img = new Image();
  img.src = src;
  div.appendChild(img);
}
let imageArray = [];

function mapImage(d) {
  d.forEach((item) => {
    item.forEach((c) => {
      imageArray.push(c.src);
    });
  });
}
mapImage([data.page1.row, data.page2.timeline]);
imageArray = imageArray.concat(data.page6.block, 'https://gw.alipayobjects.com/zos/rmsportal/CPGuYZxqYoqLAeBUknUd.png');
imageArray.forEach((src) => {
  loadImage(src);
});
