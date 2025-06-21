function swapImage(name, status){
  eval("document."+name+".src = Navs[status].src")
}

Navs = new Array(14);

Navs['articles']=new Image(60, 60)			// 1
Navs['articles'].src="/te0/images/articles.gif"
Navs['articles_over']=new Image(60, 60)			// 2
Navs['articles_over'].src="/te0/images/articles_on.gif"

Navs['clans']=new Image(60, 60)				// 3
Navs['clans'].src="/te0/images/clans.gif"
Navs['clans_over']=new Image(60, 60)			// 4
Navs['clans_over'].src="/te0/images/clans_on.gif"

Navs['events']=new Image(60, 60)			// 5
Navs['events'].src="/te0/images/events.gif"
Navs['events_over']=new Image(60, 60)			// 6
Navs['events_over'].src="/te0/images/events_on.gif"

Navs['mail']=new Image(60, 60)				// 7
Navs['mail'].src="/te0/images/mail.gif"
Navs['mail_over']=new Image(60, 60)			// 8
Navs['mail_over'].src="/te0/images/mail_on.gif"

Navs['news']=new Image(60, 60)				// 9
Navs['news'].src="/te0/images/news.gif"
Navs['news_over']=new Image(60, 60)			// 10
Navs['news_over'].src="/te0/images/news_on.gif"

Navs['objects']=new Image(60, 60)			// 11
Navs['objects'].src="/te0/images/objects.gif"
Navs['objects_over']=new Image(60, 60)			// 12
Navs['objects_over'].src="/te0/images/objects_on.gif"

Navs['people']=new Image(60, 60)			// 13
Navs['people'].src="/te0/images/people.gif"
Navs['people_over']=new Image(60, 60)			// 14
Navs['people_over'].src="/te0/images/people_on.gif"
