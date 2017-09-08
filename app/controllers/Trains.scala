package controllers


object Trains {

  val trains = List(

    Train(13, 331, List(
      TrainSection(6, "FlnHno"), 
      TrainSection(5, "HnoOrn"), 
      TrainSection(5, "BlgOrn"), 
      TrainSection(3, "BlgBlg"), 
      TrainSection(6, "BlgSau"), 
      TrainSection(3, "GtfSau"), 
      TrainSection(6, "GtfSt"), 
      TrainSection(1, "StSt"), 
      TrainSection(7, "StVhy"), 
      TrainSection(4, "HdmVhy"), 
      TrainSection(1, "HdmHdm"), 
      TrainSection(7, "HdmSnb"), 
      TrainSection(7, "AvkySnb"), 
      TrainSection(2, "AvkyAvky"), 
      TrainSection(6, "AvkyRy"), 
      TrainSection(6, "BdoRy"), 
      TrainSection(6, "BdoSl"), 
      TrainSection(3, "SlSl"), 
      TrainSection(5, "IstSl"), 
      TrainSection(7, "IstMå"), 
      TrainSection(8, "JlaMå"), 
      TrainSection(6, "BnaJla"), 
      TrainSection(4, "BnaUna"), 
      TrainSection(3, "UUna"), 
      TrainSection(7, "UU"), 
      TrainSection(3, "SäyU"), 
      TrainSection(2, "EbySäy"), 
      TrainSection(3, "EbyKn"), 
      TrainSection(1, "KnMyn"), 
      TrainSection(7, "ArncMyn"), 
      TrainSection(4, "ArncArnc"), 
      TrainSection(2, "ArncArne"), 
      TrainSection(1, "ArneBvr"), 
      TrainSection(3, "BvrSkby"), 
      TrainSection(1, "SkbyUpv"), 
      TrainSection(2, "RUpv"), 
      TrainSection(1, "HgvR"), 
      TrainSection(2, "HgvKmy"), 
      TrainSection(2, "KmyUdl"), 
      TrainSection(1, "TmöUdl"), 
      TrainSection(0, "SoTmö"), 
      TrainSection(1, "KeSo"), 
      TrainSection(4, "CstKe"), 
      TrainSection(0, "CstCst"))
    ),
    Train(14, 465, List(
      TrainSection(3, "CstKe"), 
      TrainSection(1, "KeTmö"), 
      TrainSection(1, "SoTmö"), 
      TrainSection(1, "SoUdl"), 
      TrainSection(2, "KmyUdl"), 
      TrainSection(1, "HgvKmy"), 
      TrainSection(2, "HgvR"), 
      TrainSection(2, "RUpv"), 
      TrainSection(1, "SkbyUpv"), 
      TrainSection(2, "BvrSkby"), 
      TrainSection(2, "ArneBvr"), 
      TrainSection(3, "ArncArne"), 
      TrainSection(2, "ArncArnc"))
    )
  )
}
