  #### 需求
   最近公司搞了个问卷调查的活动，用户填完问卷就能获得一次抽奖的就会，本来抽奖都是按概率来抽奖的，这种按概率的晚上挺多的相关实现方式，但是我们这个有个特定要求，就是奖品是固定的，抽奖分为三个阶段，第一阶段奖品固定个数，二等奖1个，三等奖2个，四等奖2个，五等奖15个，幸运奖180个。保证参与人数中奖率为100%。第二阶段，第三阶段我就不列出来啦，我把产品那边的需求文档放上来。
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/2019072511280397.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4MDgyMzA0,size_16,color_FFFFFF,t_70)
   产品需求放在这，之前我也没做过此类抽奖的项目，去网上查找资料，基本全是按照概率来抽奖的，没有我们的这么操蛋。
  #### 我的实现思路
  既然是抽奖，而且奖品固定，我想到的就是将所有的奖品都放到一个大容器中，用户抽奖时，抽到哪个就将相应的奖品数量从容器中移除掉一个。于是我开始对各个奖项设置为了各自抽奖编码，一等奖抽奖编码是 `99999` 二等奖是`88888`,三等奖是`77777` 一次类推，我做了下面这个表格方便查看。
  
  - 奖项数量及中奖代码
  
|奖项|一等奖|二等奖|三等奖|四等奖|五等奖| 幸运奖
|--|--|--|--|--|--|--|
|物品|华为P30|kindle阅读器|蓝牙耳机|迷你风扇|定制小礼品| 代金券或红包
|数量|1|3|5|8|40| 513
|代码|99999|88888|77777|66666|55555| 44444

数量和奖品代码搞清楚了，现在就在想那什么容器来装这些奖品代码呢？当时想过存到数据库中，现在奖品已经放到容器了，那第二个问题来了，我该如何保证这些奖品确保指定的奖品在固定的阶段被抽走呢？一共分为三阶段 `0 - 200` 、`201 - 400` 、`401 - 570` 超过570位参与者后，奖品全部都为幸运奖啦，准确的来分的话，可以分为前面三个阶段加上`570 -  ∞ `，这里就需要一个计数器来累计用户的参与人数，从而来判断该从第几阶段中抽奖，  于是想到redis中有个计数器的累加函数，想到这里同时想到redis中能存list类型的数据，所以打算放弃将数据存到数据库中，而是存到了reids中，既然分为四个阶段，我就将前面三个阶段的奖品以三个不同的key存储到redis中，以`PRIZE_COUNT_200`存放第一阶段的奖品，`PRIZE_COUNT_400`存放第二阶段的奖品，`PRIZE_COUNT_570`存放第三阶段的奖品。每次用户抽奖前，执行redis的自增函数，该函数在返回的时候回返回当前计数器的值，然后通过该值去判断该去哪个list中获取奖品,判断当前list的大小，并通过随机数生成一个该大小之间的一个整数，然后以该整数为下标去当前list中获取中奖码，拿到中奖码去数据库匹配当前奖品的余额，如果还有余额，将余额减1，并返回中奖结果。

- 第一阶段奖品（PRIZE_COUNT_200）

|奖项|一等奖|二等奖|三等奖|四等奖|五等奖| 幸运奖
|--|--|--|--|--|--|--|
|物品|华为P30|kindle阅读器|蓝牙耳机|迷你风扇|定制小礼品| 代金券或红包
|代码|99999|88888|77777|66666|55555| 44444
|数量|0|1|2|2|15| 180

- 第二阶段奖品（PRIZE_COUNT_400）

|奖项|一等奖|二等奖|三等奖|四等奖|五等奖| 幸运奖
|--|--|--|--|--|--|--|
|物品|华为P30|kindle阅读器|蓝牙耳机|迷你风扇|定制小礼品| 代金券或红包
|代码|99999|88888|77777|66666|55555| 44444
|数量|0|1|2|2|15| 180

- 第三阶段奖品（PRIZE_COUNT_570）

|奖项|一等奖|二等奖|三等奖|四等奖|五等奖| 幸运奖
|--|--|--|--|--|--|--|
|物品|华为P30|kindle阅读器|蓝牙耳机|迷你风扇|定制小礼品| 代金券或红包
|代码|99999|88888|77777|66666|55555| 44444
|数量|1|1|1|4|10| 153

#### 初始化奖品
这里我们先将抽奖代码放到各自对应的list当中，代码如下：
```java
        //计数器初始化为0，并将三个list钟的数据全部清空
        listTools.initIncreCount(RedisKeyConstant.PRIZE_COUNT);
        listTools.delete(RedisKeyConstant.PRIZE_COUNT_200);
        listTools.delete(RedisKeyConstant.PRIZE_COUNT_400);
        listTools.delete(RedisKeyConstant.PRIZE_COUNT_570);



		//获取一个200以内随机数，将重要的奖品放到幸运奖中间位置
        int temp = new Random().nextInt(170);
        /****************第一阶段*************************/
        List<String> list200 = new ArrayList<>();

        //三等奖 2个
        list200.add(LuckCodeEnum.GRADE_THREE.getCode());
        //四等奖 2个
        list200.add(LuckCodeEnum.GRADE_FOUR.getCode());

        //五等奖 15个
        for (int i = 0; i< 15; i++) {
            list200.add(LuckCodeEnum.GRADE_FIVE.getCode());
        }

        //幸运奖
        for (int j = 0; j < 180; j++) {
            list200.add(LuckCodeEnum.GRADE_LUCK.getCode());
            //放二等奖进去
            if (j == temp) {
                //二等奖 1个
                list200.add(LuckCodeEnum.GRADE_TWO.getCode());
                list200.add(LuckCodeEnum.GRADE_THREE.getCode());
                list200.add(LuckCodeEnum.GRADE_FOUR.getCode());
            }
        }
        /***********************************************/

        /*****************第二阶段***********************/
        List<String> list400 = new ArrayList<>();
        //二等奖 1个
        list400.add(LuckCodeEnum.GRADE_TWO.getCode());
        //三等奖 2个
        list400.add(LuckCodeEnum.GRADE_THREE.getCode());
        list400.add(LuckCodeEnum.GRADE_THREE.getCode());
        //四等奖 2个
        list400.add(LuckCodeEnum.GRADE_FOUR.getCode());
        list400.add(LuckCodeEnum.GRADE_FOUR.getCode());

        //五等奖 15个
        for (int i = 0; i< 15; i++) {
            list400.add(LuckCodeEnum.GRADE_FIVE.getCode());
        }

        //幸运奖
        for (int j = 0; j < 180; j++) {
            list400.add(LuckCodeEnum.GRADE_LUCK.getCode());
        }
        /**********************************************/

        /*****************第三阶段***********************/
        List<String> list570 = new ArrayList<>();

        //三等奖 2个
        list570.add(LuckCodeEnum.GRADE_THREE.getCode());
        //四等奖 4个
        list570.add(LuckCodeEnum.GRADE_FOUR.getCode());


        //五等奖 15个
        for (int i = 0; i< 10; i++) {
            list570.add(LuckCodeEnum.GRADE_FIVE.getCode());
        }

        int index = new Random().nextInt(130);
        //幸运奖
        for (int j = 0; j < 153; j++) {
            list570.add(LuckCodeEnum.GRADE_LUCK.getCode());
            if (index == j) {
                list570.add(LuckCodeEnum.GRADE_ONE.getCode());
                list570.add(LuckCodeEnum.GRADE_FOUR.getCode());

            }
            if (index +20 == j) {
                //二等奖 1个
                list570.add(LuckCodeEnum.GRADE_TWO.getCode());
                list570.add(LuckCodeEnum.GRADE_FOUR.getCode());
                list570.add(LuckCodeEnum.GRADE_FOUR.getCode());
            }
        }

        listTools.leftPushList(RedisKeyConstant.PRIZE_COUNT_200,list200);
        listTools.leftPushList(RedisKeyConstant.PRIZE_COUNT_400,list400);
        listTools.leftPushList(RedisKeyConstant.PRIZE_COUNT_570,list570);
        /**********************************************/
```
奖品已经放到了三个容器当前，现在就是将奖品数量持久化，放到数据库当中。表结构以及数量如下，这里幸运奖设置为9999是因为来抽奖的用户是未知的，第570位以后的用户，全部为幸运奖。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190725144443808.png)

#### 开始抽奖
假如当前用户为第208位来，当前计数器为 `208` 我们就该去`PRIZE_COUNT_400`该集合中获取奖品，但是奖品该怎么拿，并且确保每个奖品都被抽光呢？我个人的想法是，首先拿到`PRIZE_COUNT_400`该值得集合的`size`,然后通过随机数生成一个`size`以内的int类型的数，然后通过集合下标获取到中奖的号码。
```java
//通过redis拿到当前的prize_count的大小
Long prizeCount = listTools.increCount(RedisKeyConstant.PRIZE_COUNT);
//判断该去哪个容器中获取奖品
if (prizeCount <= 570) {
    String prizeCodeKey = null;
    if (prizeCount <= 200) {
        prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_200;
    } else if (prizeCount <= 400) {
        prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_400;
    } else if (prizeCount <= 570) {
        prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_570;
    }
 //获取到集合的大小
Long size = listTools.getListSize(prizeCodeKey);
 // 随机算法，获取随机下标并取出对应的奖品编码
 int index = new Random().nextInt(size.intValue());
 //获取到的中奖号码
 final String code = listTools.getIndexValue(prizeCodeKey,index);
```
通过获取到的中奖`code`，我们假如这里是`88888`去数据库查询当前奖品的余额数量， 如果余额大于0，则让该奖品数量减少1份，然后把redis中`PRIZE_COUNT_400`list中通过` listOperations.remove(key,1,value);`移除掉第一个遇到的`88888`,这样当前list的size就会减少1，按照该方法一直抽下去，list的size会越来越小，抽奖人数计数器等于400的时候，该list中的奖品全部被抽完。

#### 抽奖核心代码
```java
       //判断该用户是否已经中奖
        boolean flag = userService.isCanLuckDraw(name);
        if (!flag) {
            log.warn("{},已经抽过奖，无需重复抽奖",name);
            throw new LuckDrawException(ResultCode.LUCK_DRAW_AGAIN);
        }
        boolean isOk = false;
        //通过redis拿到当前的prize_count的大小
        Long prizeCount = listTools.increCount(RedisKeyConstant.PRIZE_COUNT);

        //默认幸运奖
        String luckCode = LuckCodeEnum.GRADE_LUCK.getCode();
        if (prizeCount <= 570) {
            String prizeCodeKey = null;
            if (prizeCount <= 200) {
                prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_200;
            } else if (prizeCount <= 400) {
                prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_400;
            } else if (prizeCount <= 570) {
                prizeCodeKey = RedisKeyConstant.PRIZE_COUNT_570;
            }

            //这里写循环是为了补偿那些拿到数据库没有的库存的奖品
            synchronized (PrizeController.class) {
                for (int i = 0; i < 3; i++) {
                    Long size = listTools.getListSize(prizeCodeKey);
                    // 随机算法，获取随机下标并取出对应的奖品编码
                    int index = new Random().nextInt(size.intValue());
                    //获取到的中奖号码
                    final String code = listTools.getIndexValue(prizeCodeKey,index);
                    //将中奖号码赋予给外层的中奖号码
                    luckCode = code;
                    //执行的结果，返回true表示数据库还有余额
                    //取数据库核对该奖品是否还有余额，有自减 没有重新进行随机算法 最后将中奖信息插入数据库
                    isOk = prizeService.resultTrue(name,code);
                    if (isOk) {
                        //移除redis中对应list中的一个奖项
                        listTools.removeFirstValue(prizeCodeKey,code);
                        break;
                    }
                    log.error("{},获取{}奖品，库存余额不足 这是第几次获取:{}",name,luckCode,i+1);
                }
            }
        }
        //如果该代码执行了说明拿出的奖品数据库余额不足，直接返回幸运奖
        if (!isOk) {
            if (prizeCount > 570)
                log.warn("{}，奖品已经全部发放完毕，直接返回幸运奖",name);
            else
                log.error("{}，拿到的奖品数据库余额不足，直接返回幸运奖",name);
            prizeService.resultTrue(name, luckCode);
        }
        return ResultUtils.success("您中的奖为 ["+luckCode+"]");
```

---
这就是我自己的自创该次抽奖的想法，有什么说的不对的地方请各位能够提出来一起讨论一下。

项目源码：https://github.com/niezhiliang/luck-draw


