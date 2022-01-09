package org.jeecg.modules.exam.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.exam.entity.Score;
import org.jeecg.modules.exam.entity.Total;
import org.jeecg.modules.exam.mapper.ExamMapper;
import org.jeecg.modules.exam.mapper.ScoreMapper;
import org.jeecg.modules.exam.mapper.TotalMapper;
import org.jeecg.modules.exam.service.ITotalService;
import org.jeecg.modules.exam.vo.ScoreExamUserVo;
import org.jeecg.modules.exam.vo.TotalUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description: 总成绩与排名表
 * @Author: jeecg-boot
 * @Date:   2019-08-08
 * @Version: V1.0
 */
@Slf4j
@Service
public class TotalServiceImpl extends ServiceImpl<TotalMapper, Total> implements ITotalService {
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private TotalMapper totalMapper;
    @Autowired
    private ExamMapper examMapper;

    @Override
    public boolean calcByExamName(String examName){
        totalMapper.delete(new QueryWrapper<Total>().eq("exam_name", examName));

        int totalNum = examMapper.getSessionNumByExamName(examName);

        try{
            List<ScoreExamUserVo> scoreResult = scoreMapper.queryByExamName(examName);
            if (scoreResult.size() <= 0){
                log.info("目前没有任何考试成绩");
                return true;
            }

            ArrayList<String> userList = new ArrayList<>();
            //计算每个考生的单项科目成绩及排名
            HashMap<String, ArrayList<ScoreExamUserVo>> scoreForSession = new HashMap<>();
            for(ScoreExamUserVo score: scoreResult){
                ArrayList<ScoreExamUserVo> tempList =  new ArrayList<>();
                if(scoreForSession.containsKey(score.getExamId())){
                    tempList = scoreForSession.get(score.getExamId());
                }
                tempList.add(score);
                scoreForSession.put(score.getExamId(), tempList);

                //获取用户id列表
                if(!userList.contains(score.getUserId())){
                    userList.add(score.getUserId());
                }
            }

            HashMap<String, JSONObject> userSessionRank = new HashMap<>();
            for(String examId: scoreForSession.keySet()){
                Map<String, Map<String, String>> sessionRank = getRankForSession(scoreForSession.get(examId));
                for(String userId: sessionRank.keySet()){
//                    Map<String, Object> tmp = new HashMap<>();
                    JSONObject tmp = new JSONObject();
                    if (userSessionRank.containsKey(userId)){
                        tmp  = userSessionRank.get(userId);
                    }
                    tmp.put(examId, sessionRank.get(userId));
                    userSessionRank.put(userId, tmp);
                }
            }

            log.info("考生单项成绩排名是:" + userSessionRank.toString());

            //计算考生总成绩
            HashMap<String, Double> userTotalScore = new HashMap<>();
            HashMap<String, Integer> userCompleteNumMap = new HashMap<>();
            for(ScoreExamUserVo score: scoreResult){
                double totalScore = 0.0;
                int completeNum = 0;
                if (userTotalScore.containsKey(score.getUserId())){
                    totalScore = userTotalScore.get(score.getUserId());
                }
                if (userCompleteNumMap.containsKey(score.getUserId())){
                    completeNum = userCompleteNumMap.get(score.getUserId());
                }
                totalScore += score.getScore();
                userTotalScore.put(score.getUserId(), totalScore);
                userCompleteNumMap.put(score.getUserId(), completeNum+1);
            }
            log.info("考生总分为:"+ userTotalScore);
            log.info("考生完成科目数目:"+ userCompleteNumMap);

            //找出未完成所有科目考试或者总分小于100的考生列表
            ArrayList<String> noRankUserList = new ArrayList<>();
            for(String userId: userTotalScore.keySet()){
                if (userTotalScore.get(userId) < 100){
                    noRankUserList.add(userId);
                }
            }
            for(String userId: userCompleteNumMap.keySet()){
                if (userCompleteNumMap.get(userId) < totalNum){
                    if (!noRankUserList.contains(userId)){
                        noRankUserList.add(userId);
                    }
                }
            }

            for(String userId: noRankUserList){
                Total total = new Total();
                total.setExamName(examName);
                total.setUserId(userId);
                total.setTotal(userTotalScore.get(userId));
                total.setRanking(999999);//不排名考生设置最大值
                total.setCompleteNum(userCompleteNumMap.get(userId));
                total.setDetail(userSessionRank.get(userId).toString());
                totalMapper.insert(total);
            }

            //按总分为考生进行排序
            HashMap<String, Double> rankUserTotalScore = new HashMap<>();
            for (String userId: userTotalScore.keySet()){
                if (noRankUserList.contains(userId)) continue;
                rankUserTotalScore.put(userId, userTotalScore.get(userId));
            }
            HashMap<String, Integer> userTotalRank = this.getRankForTotal(rankUserTotalScore);
            
            for(String userId: rankUserTotalScore.keySet()){
                Total total = new Total();
                total.setExamName(examName);
                total.setUserId(userId);
                total.setTotal(userTotalScore.get(userId));
                total.setRanking(userTotalRank.get(userId));
                total.setCompleteNum(userCompleteNumMap.get(userId));
                total.setDetail(userSessionRank.get(userId).toString());
                totalMapper.insert(total);
            }

        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    public HashMap<String, Integer> getRankForTotal(HashMap<String, Double> userTotalScore) {
        HashMap<String, Integer> userTotalRank = new HashMap<>();
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(userTotalScore.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if(o1.getValue().compareTo(o2.getValue()) < 0){
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        log.info("list is:" + list);
        for(int i=0, s=list.size(); i<s;i++)
        {
            if(i>0 && list.get(i).getValue().compareTo(list.get(i-1).getValue()) == 0){
                log.info("get here score is " + list.get(i).getValue());
                userTotalRank.put(list.get(i).getKey(), userTotalRank.get(list.get(i-1).getKey()));
            }else{
                userTotalRank.put(list.get(i).getKey(), i+1);
            }
        }
        log.info("考生总分排名为：" + userTotalRank);

        return userTotalRank;
    }
    public Map<String, Map<String, String>> getRankForSession(ArrayList<ScoreExamUserVo> scoreExamUserVoArrayList) {
        Comparator<ScoreExamUserVo> comparator=new Comparator<ScoreExamUserVo>() {
            @Override
            public int compare(ScoreExamUserVo o1, ScoreExamUserVo o2) {
                if(o1.getScore() < o2.getScore()){
                    return 1;
                }else if(o1.getScore() == o2.getScore()){
                    return 0;
                }else{
                    return -1;
                }
            }
        };

        scoreExamUserVoArrayList.sort(comparator);
        Map<String, Map<String, String>> rankForUser = new HashMap<>();
        for(int i=0, s=scoreExamUserVoArrayList.size(); i<s;i++)
        {
            Map<String, String> detailTmp = new HashMap<>();
            detailTmp.put("score", scoreExamUserVoArrayList.get(i).getScore().toString());
//            detailTmp.put("rank", i+1);
//            rankForUser.put(scoreExamUserVoArrayList.get(i).getUserId(), detailTmp);
            if(i>0 && scoreExamUserVoArrayList.get(i).getScore().compareTo(scoreExamUserVoArrayList.get(i-1).getScore()) == 0){
                String userIdTmp = scoreExamUserVoArrayList.get(i-1).getUserId();
                if(rankForUser.containsKey(userIdTmp)){
                    Map<String, String> tmp = rankForUser.get(userIdTmp);
                    detailTmp.put("rank", tmp.get("rank"));
                }
                rankForUser.put(scoreExamUserVoArrayList.get(i).getUserId(), detailTmp);
            }else{
                detailTmp.put("rank", String.valueOf(i+1));
                rankForUser.put(scoreExamUserVoArrayList.get(i).getUserId(), detailTmp);
            }
        }
        return rankForUser;
    }

    @Override
    public IPage<TotalUserVo> queryPageListByExamName(Page<TotalUserVo> page, String examName, String provcode){
        return this.baseMapper.queryPageListByExamName(page,examName,provcode);
    }

    @Override
    public List<TotalUserVo> queryListByExamName(String examName, String provcode){
        return this.baseMapper.queryListByExamName(examName,provcode);
    }

    @Override
    public List<TotalUserVo> queryListWithRank(String examName, Integer start, Integer end){
        return this.baseMapper.queryListWithRank(examName,start,end);
    }


}
