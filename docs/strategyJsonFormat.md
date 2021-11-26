# 策略配置说明

每个聚合广告位均可通过`setData(String strategyJson) `方法设置策略信息，其中 `strategyJson`参数为SDK的策略配置信息，其json结构示例如下：

```
{
  "rules": [
    {
      "tag": "A",
      "sort": [
        1,
        3
      ],
      "percent": 30
    },
    {
      "tag": "B",
      "sort": [
        2,
        4
      ],
      "percent": 70
    }
  ],
  "suppliers": [
    {
      "tag": "csj",
      "adspotId": "887477661",
      "appId": "5051624",
      "index": 1
    },
    {
      "tag": "ylh",
      "adspotId": "2001447730515391",
      "appId": "1101152570",
      "index": 2
    },
    {
      "tag": "ks",
      "adspotId": "4000000042",
      "appId": "90009",
      "index": 3
    },
    {
      "tag": "bd",
      "adspotId": "2058622",
      "appId": "e866cfb0",
      "index": 4
    }
  ]
}
```


* strategyJson包含内容


|字段名	| 字段类型 |含义
|:--- |:--- |:--- |
|suppliers | List&lt;Supplier> | **SDK渠道详细信息**，相当于“广告池”，Supplier对象含义见下表
|rules | List&lt;Rule> | **策略规则配置数组**，将suppliers“广告池”中的广告以约定的规则进行排序和分发。可分为多组执行广告策略，组内变量主要是SDK顺序排序方式和切分比例。Rule对象含义见下表


* Rule 对象包含内容

|字段名	| 字段类型 |含义
|:--- |:--- |:--- |
|tag	|String	|**策略组唯一标记**，用于区分标记不同组的执行情况
| sort | List&lt;Integer> | **广告SDK执行顺序表**，依照组内顺序，优先级从高到低，组内成员对应suppliers字段中的index变量
|percent | int | **流量占比值**，SDK内部会根据多组内配置的值，自动计算比例，执行流量百分比的分发模式，**建议使用百分值**。<br/>比如上述json示例中配置的含义为：在发起请求后，有**30%**的概率执行策略组A中配置，按照1->3的顺序依次执行广告加载；**70%**的概率执行策略组A中配置，按照2->4的顺序依次执行广告加载。<br/>如果A、B两组中percent配置值分别为**201、799**，代表**20.1%**的概率执行A，**79.9%**的概率执行B。<br/>如果A、B两组中percent配置值分别为**2、3**，代表**40%**的概率执行A，**60%**的概率执行B。<br/>如果**仅有一组A**，不论percent按照多少设置，都默认**100%**的流量执行A


* Supplier 对象包含内容

字段名	| 字段类型 |含义
|:--- |:--- |:--- |
|index	|int|**唯一坐标**，用来和rules信息内sort字段关联，确定广告执行顺序
|tag | String | **SDK类别标识**，<br/>"csj"代表头条-穿山甲SDK<br/>"ylh"代表腾讯-优量汇SDK（前广点通）<br/>"ks"代表快手-快手联盟SDK<br/>"bd"代表百度-百青藤SDK
|adspotId | String | **广告位id**，在变现SDK后台申请到的具体广告位id
|appId | String | **应用id**，在变现SDK后台申请到的应用id



