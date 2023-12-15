<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%

// Setting the page parameter
String targetPage="";

String nextLabel = localization.getString("common.next.label");
String previousLabel = localization.getString("common.previous.label");

// Start computing the pagination

int start = 0;
start = (pageParameter - 1) * maxItemsPerPage; 			

if (pageParameter <= 0) pageParameter = 1;
int prev = pageParameter - 1;
int next = pageParameter + 1;
Double lastpage = Math.ceil(totalRes/(double)maxItemsPerPage);
int lpm1 = lastpage.intValue() - 1;

int i = 0;
String pagination = "";
if(lastpage > 1){	
    pagination += "<ul class=\"pagination\">";

    if (pageParameter > 1){
        pagination += "<li><a aria-label=\""+previousLabel+"\" href=\""+targetPage+"?q="+searchTerms+"&p="+prev+"\"><span aria-hidden=\"true\">"+previousLabel+"</span></a></li>";
    }else{
        pagination += "<li class=\"disabled\"><a aria-label=\""+previousLabel+"\"><span aria-hidden=\"true\">"+previousLabel+"</span></a></li>";	
    }
    
    if (lastpage < 7 + (maxItemsAdjacent * 2)){	
        for (i = 1; i <= lastpage; i++){
            if (i == pageParameter){
                pagination+= "<li class=\"active\"><a>"+i+"<span class=\"sr-only\"> (current)</span></a></li>";
            }else{
                pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+i+"\">"+i+"</a></li>";
            }
        }    
    }else if(lastpage > 5 + (maxItemsAdjacent * 2)){

        if(pageParameter < 1 + (maxItemsAdjacent * 2)){
            for (i= 1; i < 4 + (maxItemsAdjacent * 2); i++){
                if (i == pageParameter){
                    pagination+= "<li class=\"active\"><a>"+i+"<span class=\"sr-only\"> (current)</span></a></li>";
                }else{
                    pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+i+"\">"+i+"</a></li>";					
                }
            }
            
            pagination+= "<li><a>...</a></li>";
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+lpm1+"\">"+lpm1+"</a></li>";
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+lastpage+"\">"+lastpage+"</a></li>";		
        
        }else if(lastpage - (maxItemsAdjacent * 2) > pageParameter && pageParameter > (maxItemsAdjacent * 2)){
            
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p=1\">1</a></li>";
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p=2\">2</a></li>";
            pagination+= "<li><a>...</a></li>";
            
            for (i = pageParameter - maxItemsAdjacent; i <= pageParameter + maxItemsAdjacent; i++){
                if (i == pageParameter){
                    pagination+= "<li class=\"active\"><a>"+i+"<span class=\"sr-only\"> (current)</span></a></li>";
                }else{
                    pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+i+"\">"+i+"</a></li>";					
                }
            }
            pagination+= "<li><a>...</a></li>";
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+lpm1+"\">"+lpm1+"</a></li>";
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+lastpage+"\">"+lastpage+"</a></li>";		
        
        }else{
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p=1\">1</a></li>";
            pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p=2\">2</a></li>";
            pagination+= "<li><a>...</a></li>";
            for (i = lastpage.intValue() - (2 + (maxItemsAdjacent * 2)); i <= lastpage; i++){
                if (i == pageParameter){
                    pagination+= "<li class=\"active\"><a>"+i+"<span class=\"sr-only\"> (current)</span></a></li>";
                }else{
                    pagination+= "<li><a href=\""+targetPage+"?q="+searchTerms+"&p="+i+"\">"+i+"</a></li>";					
                }
            }
        }
    }

    if (pageParameter < i - 1){
        pagination+= "<li><a aria-label=\""+nextLabel+"\" href=\""+targetPage+"?q="+searchTerms+"&p="+next+"\"><span aria-hidden=\"true\">"+nextLabel+"</span></a></li>";
    }else{
        pagination+= "<li class=\"disabled\"><a aria-label=\""+nextLabel+"\"><span aria-hidden=\"true\">"+nextLabel+"</span></a></li>";
    }

    pagination+= "</ul>\n";		
}
%>
<%=pagination%>