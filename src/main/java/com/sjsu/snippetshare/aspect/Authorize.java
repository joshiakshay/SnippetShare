package com.sjsu.snippetshare.aspect;

import com.sjsu.snippetshare.domain.Board;
import com.sjsu.snippetshare.domain.Comment;
import com.sjsu.snippetshare.domain.Snippet;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 5/5/15.
 */

@Aspect
public class Authorize {

    @Around("execution(public* com.sjsu.snippetshare.service.BoardHandler.getAllBoards(..))")
    public Object doBoardAccessCheck(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String userId = (String) args[0];
        boolean accessPrivacy = (Boolean) args[1];
        System.out.println("Before");
        System.out.println(accessPrivacy);
        System.out.println("In aspect");
        ArrayList<ArrayList<Board>> masterList = (ArrayList<ArrayList<Board>>) pjp.proceed();
        ArrayList<Board> boards = masterList.get(0);
        ArrayList<ArrayList<Board>> allBoards = new ArrayList<ArrayList<Board>>();
        if (accessPrivacy) {
            ArrayList<Board> ownedBoards = new ArrayList<Board>();
            ArrayList<Board> sharedBoards = new ArrayList<Board>();
            for (Board board : boards) {
                if (board.getBoardOwner().equals(userId)) {
                    ownedBoards.add(board);
                } else {
                    List<String> accessList = board.getAccessList();
                    if (accessList != null) {
                        for (String accessPerson : accessList) {
                            if (userId.equals(accessPerson)) {
                                sharedBoards.add(board);
                            }
                        }
                    }
                }
            }
            allBoards.add(0, ownedBoards);
            allBoards.add(1, sharedBoards);
        } else {
            ArrayList<Board> javaBoards = new ArrayList<Board>();
            ArrayList<Board> scalaBoards = new ArrayList<Board>();
            ArrayList<Board> ccppBoards = new ArrayList<Board>();
            ArrayList<Board> htmlBoards = new ArrayList<Board>();
            ArrayList<Board> pppBoards = new ArrayList<Board>();
            for (Board board : boards) {
                boolean getBoard = false;
                if (board.getPrivacy().equals("Public")) {
                    getBoard = true;
                } else {
                    if (board.getBoardOwner().equals(userId)) {
                        getBoard = true;
                    } else {
                        List<String> accessList = board.getAccessList();
                        if (accessList != null) {
                            for (String accessPerson : accessList) {
                                if (userId.equals(accessPerson)) {
                                    getBoard = true;
                                }
                            }
                        }
                    }
                }
                if (getBoard) {
                    if (board.getCategory().equals("Java")) {
                        javaBoards.add(board);
                    } else if (board.getCategory().equals("Scala")) {
                        scalaBoards.add(board);
                    } else if (board.getCategory().equals("PHP / Perl / Python")) {
                        pppBoards.add(board);
                    } else if (board.getCategory().equals("C / C++")) {
                        ccppBoards.add(board);
                    } else if (board.getCategory().equals("HTML / JavaScript")) {
                        htmlBoards.add(board);
                    }
                }
            }
            allBoards.add(0, javaBoards);
            allBoards.add(1, scalaBoards);
            allBoards.add(2, ccppBoards);
            allBoards.add(3, htmlBoards);
            allBoards.add(4, pppBoards);
        }
        return allBoards;
    }

    @Around("execution(public* com.sjsu.snippetshare.service.SnippetHandler.getAllSnippets(..))")
    public Object doSnippetAccessCheck(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String boardId = (String) args[0];
        String userId = (String) args[1];
        ArrayList<ArrayList<Snippet>> master = (ArrayList<ArrayList<Snippet>>) pjp.proceed();
        ArrayList<Snippet> snippets = master.get(0);
        ArrayList<Snippet> editableSnippetList = new ArrayList<Snippet>();
        ArrayList<Snippet> nonEditableSnippetList = new ArrayList<Snippet>();
        for (Snippet snippet : snippets) {
            if (userId.equals(snippet.getOwnerId())) {
                editableSnippetList.add(snippet);
            } else {
                nonEditableSnippetList.add(snippet);
            }
        }
        ArrayList<ArrayList<Snippet>> masterSnippetList = new ArrayList<ArrayList<Snippet>>();
        masterSnippetList.add(0, editableSnippetList);
        masterSnippetList.add(1, nonEditableSnippetList);
        return masterSnippetList;
    }

    @Around("execution(public* com.sjsu.snippetshare.service.CommentsHandler.getAllComments(..))")
    public Object doCommentAccessCheck(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String userId = (String) args[0];
        String boardId = (String) args[1];
        String snippetId = (String) args[2];
        List<Comment> comments = (List<Comment>) pjp.proceed();
        List<Comment> editableCommentList = new ArrayList<Comment>();
        List<Comment> nonEditableCommentList = new ArrayList<Comment>();
        for (Comment comment : comments) {
            if (userId.equals(comment.getOwnerId())) {
                editableCommentList.add(comment);
            } else {
                nonEditableCommentList.add(comment);
            }
        }
        List<List<Comment>> masterCommentList = new ArrayList<List<Comment>>();
        masterCommentList.add(editableCommentList);
        masterCommentList.add(nonEditableCommentList);
        return masterCommentList;
    }
}
