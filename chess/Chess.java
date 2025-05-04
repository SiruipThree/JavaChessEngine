// Programmer: Three Peng NetID:sp2269 || Sarpreet Singh NetID: ss4426
package chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chess {

    enum Player { white, black }

    private static Player currentPlayer = Player.white;
    private static Map<String, ReturnPiece> board = new HashMap<>(); // key: position, value: piece at position
    private static List<ReturnPiece> piecesOnBoard = new ArrayList<>();// list of pieces on the board
    private static String enPassantSquare = null;//special situation for pawn
    private static boolean whiteKingMoved = false; // determine if white king has moved 王车易位
    private static boolean blackKingMoved = false;
    private static boolean[] whiteRooksMoved = { false, false }; // determine if white rooks have moved
    private static boolean[] blackRooksMoved = { false, false };

    public static void checkIntegrity() {
        // for (ReturnPiece piece : piecesOnBoard) {
        //     if (!board.containsKey(piece.pieceFile.name().toLowerCase() + piece.pieceRank)) {
        //         throw new RuntimeException("Piece on board not found in board");
        //     }
        // }
        // for (String position : board.keySet()) {
        //     if (board.get(position) == null) {
        //         throw new RuntimeException("Piece on board is null");
        //     }
        // }
        // for (int i = 0; i < 8; i++) {
        //     for (int j = 1; j <= 8; j++) {
        //         String position = String.format("%c%d", 'a' + i, j);
        //         if (!board.containsKey(position)) {
        //             continue;
        //         }
        //         if (board.get(position) == null) {
        //             throw new RuntimeException("Piece on board is null");
        //         }
        //         ReturnPiece piece = board.get(position);
        //         if (!piece.pieceFile.name().equalsIgnoreCase(position.substring(0, 1))) {
        //             throw new RuntimeException("Piece on board has wrong file");
        //         }
        //         if (piece.pieceRank != j) {
        //             throw new RuntimeException("Piece on board has wrong rank");
        //         }
        //     }
        // }
    }

    public static ReturnPlay play(String move) {
        try{

            checkIntegrity();
            ReturnPlay result = new ReturnPlay();
            result.piecesOnBoard = new ArrayList<>(piecesOnBoard);
            move = move.trim().toLowerCase();

            if(move.equals("resign")) {
                result.message = currentPlayer == Player.white ? 
                    ReturnPlay.Message.RESIGN_BLACK_WINS : 
                    ReturnPlay.Message.RESIGN_WHITE_WINS;
                Chess.start();
                return result;
            } // if the move is resign, the game is over

            boolean drawOffer = move.endsWith("draw?");
            if(drawOffer) {
                move = move.substring(0, move.length() - 5).trim();
                if (move.isEmpty()) {
                    result.message = ReturnPlay.Message.ILLEGAL_MOVE;
                    return result; // if the move is illegal, return illegal move
                }
            }// if the move is draw, the game is over

            String[] parts = move.split("\\s+");
            if(parts.length < 2) {
                result.message = ReturnPlay.Message.ILLEGAL_MOVE;
                return result;
            }

            String from = parts[0];
            String to = parts[1];
            String promotion = parts.length > 2 ? parts[2].toUpperCase() : null;

            ReturnPiece piece = board.get(from);
            if(piece == null || !isValidMove(piece, from, to, promotion, currentPlayer, true, false)) {
                result.message = ReturnPlay.Message.ILLEGAL_MOVE;
                checkIntegrity();
                return result;
            }// if the move is illegal, return illegal move

            checkIntegrity();
            ReturnPiece targetPiece = board.get(to);
            ReturnPiece currenReturnPiece= board.get(from);
            board.remove(to);
            board.put(to, currenReturnPiece);
            board.remove(from);
            currenReturnPiece.pieceFile = ReturnPiece.PieceFile.valueOf(to.substring(0, 1).toLowerCase());
            currenReturnPiece.pieceRank = Integer.parseInt(to.substring(1));
            boolean isCheckAfterMove = isCheck(currentPlayer);
            board.remove(to);
            board.put(from, currenReturnPiece);
            currenReturnPiece.pieceFile = ReturnPiece.PieceFile.valueOf(from.substring(0, 1).toLowerCase());
            currenReturnPiece.pieceRank = Integer.parseInt(from.substring(1));
            if(targetPiece != null) {
                board.put(to, targetPiece);
            }
            if(isCheckAfterMove) {
                result.message = ReturnPlay.Message.ILLEGAL_MOVE;
                checkIntegrity();
                return result;
            }// check if the move is legal
            checkIntegrity();
            executeMove(piece, from, to, promotion);
            updateCastlingStatus(piece, from);
            checkIntegrity();

            Player opponent = currentPlayer == Player.white ? Player.black : Player.white;
            currentPlayer = opponent;
            if(isCheck(opponent)) {
                if(isCheckmate(opponent)) {
                    result.message = currentPlayer == Player.black ? 
                        ReturnPlay.Message.CHECKMATE_WHITE_WINS : 
                        ReturnPlay.Message.CHECKMATE_BLACK_WINS;
                } else {
                    result.message = ReturnPlay.Message.CHECK;
                }
            }
            checkIntegrity();


            result.piecesOnBoard = new ArrayList<>(board.values());

            if(drawOffer) {
                result.message = ReturnPlay.Message.DRAW;
                Chess.start();
            }
            checkIntegrity();

            return result;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch(Exception e){
            ReturnPlay result = new ReturnPlay();
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            System.out.println("handling exception: " + e + " from position:\n" + e.getStackTrace());
            checkIntegrity();
            return result;
        }
    }


    public static void start() {
        currentPlayer = Player.white;
        board.clear();
        piecesOnBoard.clear();
        enPassantSquare = null;
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRooksMoved = new boolean[] { false, false };
        blackRooksMoved = new boolean[] { false, false };

        addPiece("a1", ReturnPiece.PieceType.WR);
        addPiece("b1", ReturnPiece.PieceType.WN);
        addPiece("c1", ReturnPiece.PieceType.WB);
        addPiece("d1", ReturnPiece.PieceType.WQ);
        addPiece("e1", ReturnPiece.PieceType.WK);
        addPiece("f1", ReturnPiece.PieceType.WB);
        addPiece("g1", ReturnPiece.PieceType.WN);
        addPiece("h1", ReturnPiece.PieceType.WR);
        for(char c = 'a'; c <= 'h'; c++) {
            addPiece(c + "2", ReturnPiece.PieceType.WP);
        }

        addPiece("a8", ReturnPiece.PieceType.BR);
        addPiece("b8", ReturnPiece.PieceType.BN);
        addPiece("c8", ReturnPiece.PieceType.BB);
        addPiece("d8", ReturnPiece.PieceType.BQ);
        addPiece("e8", ReturnPiece.PieceType.BK);
        addPiece("f8", ReturnPiece.PieceType.BB);
        addPiece("g8", ReturnPiece.PieceType.BN);
        addPiece("h8", ReturnPiece.PieceType.BR);
        for(char c = 'a'; c <= 'h'; c++) {
            addPiece(c + "7", ReturnPiece.PieceType.BP);
        }
    }

    private static void addPiece(String position, ReturnPiece.PieceType type) {
        ReturnPiece piece = new ReturnPiece();
        piece.pieceType = type;
        piece.pieceFile = ReturnPiece.PieceFile.valueOf(position.substring(0, 1).toLowerCase());
        piece.pieceRank = Integer.parseInt(position.substring(1));
        board.put(position, piece);
        piecesOnBoard.add(piece);
    }

    private static boolean isValidMove(ReturnPiece piece, String from, String to, String promotion, Player player, boolean allowCastling, boolean preCheck) {
        //if(from.equals("c7") && to.equals("c6")){
            //System.out.println("TEST");
        //}
        //System.out.println("test2"+promotion+"test");
        if (from.equals(to)) {
            return false;
        }
        if (promotion != null && (!promotion.matches("[NBRQ]")|| !piece.pieceType.name().endsWith("P"))) {
            return false;
        }
        if((player == Player.white && piece.pieceType.name().startsWith("B")) ||
            (player == Player.black && piece.pieceType.name().startsWith("W"))) {
                //System.out.println("current player: " + currentPlayer);
            return false;
        }

        //int fromFile = from.charAt(0) - 'a';
        //int fromRank = Integer.parseInt(from.substring(1));
        int toFile = to.charAt(0) - 'a';
        int toRank = Integer.parseInt(to.substring(1));

        if(toFile < 0 || toFile > 7 || toRank < 1 || toRank > 8) return false;

        ReturnPiece destPiece = board.get(to);
        if(!preCheck && destPiece != null && destPiece.pieceType.name().charAt(0) == 
            piece.pieceType.name().charAt(0)) return false;

        switch(piece.pieceType) {
            case WP: return isValidWhitePawnMove(from, to, preCheck);
            case BP: return isValidBlackPawnMove(from, to, preCheck);
            case WN: case BN: return isValidKnightMove(from, to);
            case WB: case BB: return isValidBishopMove(from, to);
            case WR: case BR: return isValidRookMove(from, to);
            case WQ: case BQ: return isValidQueenMove(from, to);
            case WK: case BK: return isValidKingMove(piece, from, to, allowCastling, preCheck);
            default: return false;
        }
    }

    private static boolean isValidWhitePawnMove(String from, String to, boolean preCheck) {
        int fromFile = from.charAt(0) - 'a';
        int fromRank = Integer.parseInt(from.substring(1));
        int toFile = to.charAt(0) - 'a';
        int toRank = Integer.parseInt(to.substring(1));
        
        if(fromFile == toFile) {
            if(toRank == fromRank + 1) {
                return !preCheck && board.get(to) == null;
            } else if(toRank == fromRank + 2 && fromRank == 2) {
                String midSquare = from.charAt(0) + String.valueOf(fromRank + 1);
                return !preCheck && board.get(midSquare) == null && board.get(to) == null;
            }
        }
        else if(Math.abs(toFile - fromFile) == 1 && toRank == fromRank + 1) {
            if(preCheck || board.containsKey(to) || to.equals(enPassantSquare)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isValidBlackPawnMove(String from, String to, boolean preCheck) {
        //if(from.equals("c7") && to.equals("c6")){
           // System.out.println("c7 to c6");
        //}
        int fromFile = from.charAt(0) - 'a';
        int fromRank = Integer.parseInt(from.substring(1));
        int toFile = to.charAt(0) - 'a';
        int toRank = Integer.parseInt(to.substring(1));
        
        if(fromFile == toFile) {
            if(toRank == fromRank - 1) {
                return !preCheck && board.get(to) == null;
            } else if(toRank == fromRank - 2 && fromRank == 7) {
                String midSquare = from.charAt(0) + String.valueOf(fromRank - 1);
                return !preCheck && board.get(midSquare) == null && board.get(to) == null;
            }
        }
        else if(Math.abs(toFile - fromFile) == 1 && toRank == fromRank - 1) {
            if(preCheck||board.containsKey(to) || to.equals(enPassantSquare)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isValidKnightMove(String from, String to) {
        int dx = Math.abs(to.charAt(0) - from.charAt(0));
        int dy = Math.abs(Integer.parseInt(to.substring(1)) - Integer.parseInt(from.substring(1)));
        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }
    
    private static boolean isValidBishopMove(String from, String to) {
        int fromFile = from.charAt(0) - 'a';
        int fromRank = Integer.parseInt(from.substring(1));
        int toFile = to.charAt(0) - 'a';
        int toRank = Integer.parseInt(to.substring(1));
        
        if(Math.abs(toFile - fromFile) != Math.abs(toRank - fromRank)) return false;
        
        int stepX = Integer.compare(toFile, fromFile);
        int stepY = Integer.compare(toRank, fromRank);
        int steps = Math.abs(toFile - fromFile);
        
        for(int i = 1; i < steps; i++) {
            String square = String.format("%c%d", 'a' + fromFile + i * stepX, fromRank + i * stepY);
            if (board.containsKey(square)) return false;
        }
        return true;
    }
    
    private static boolean isValidRookMove(String from, String to) {
        int fromFile = from.charAt(0) - 'a';
        int fromRank = Integer.parseInt(from.substring(1));
        int toFile = to.charAt(0) - 'a';
        int toRank = Integer.parseInt(to.substring(1));
        
        if(fromFile != toFile && fromRank != toRank) return false;
        
        int stepX = Integer.compare(toFile, fromFile);
        int stepY = Integer.compare(toRank, fromRank);
        int steps = Math.max(Math.abs(toFile - fromFile), Math.abs(toRank - fromRank));
        
        for(int i = 1; i < steps; i++) {
            String square = String.format("%c%d", 'a' + fromFile + i * stepX, fromRank + i * stepY);
            if (board.containsKey(square)) return false;
        }
        return true;
    }
    
    private static boolean isValidQueenMove(String from, String to) {
        return isValidBishopMove(from, to) || isValidRookMove(from, to);
    }
    
    private static boolean isValidKingMove(ReturnPiece king, String from, String to, boolean allowCastling, boolean preCheck) {
        int dx = Math.abs(to.charAt(0) - from.charAt(0));
        int dy = Math.abs(Integer.parseInt(to.substring(1)) - Integer.parseInt(from.substring(1)));
        
        boolean isNormalMove = dx <= 1 && dy <= 1;

        if (isNormalMove) {
            Player opponent = currentPlayer == Player.white ? Player.black : Player.white;
            return preCheck || !isSquareUnderAttack(to, opponent, false, true);
        } else {
            return allowCastling && isValidCastling(king, from, to);
        }
    }
    
    private static void executeMove(ReturnPiece piece, String from, String to, String promotion) {
        if((piece.pieceType == ReturnPiece.PieceType.WP || piece.pieceType == ReturnPiece.PieceType.BP) 
            && to.equals(enPassantSquare)) {
            int captureRank = piece.pieceType == ReturnPiece.PieceType.WP ? 
                Integer.parseInt(to.substring(1)) - 1 : 
                Integer.parseInt(to.substring(1)) + 1;
            String capturedSquare = to.charAt(0) + String.valueOf(captureRank);
            board.remove(capturedSquare);
            piecesOnBoard.removeIf(p -> 
                p.pieceFile.name().equalsIgnoreCase(capturedSquare.substring(0, 1)) && 
                p.pieceRank == Integer.parseInt(capturedSquare.substring(1)));
        }
        
        if(piece.pieceType == ReturnPiece.PieceType.WK || piece.pieceType == ReturnPiece.PieceType.BK) {
            int dx = to.charAt(0) - from.charAt(0);
            if(Math.abs(dx) == 2) {
                String rookFrom = dx > 0 ? "h" + from.substring(1) : "a" + from.substring(1);
                String rookTo = dx > 0 ? "f" + from.substring(1) : "d" + from.substring(1);
                ReturnPiece rook = board.remove(rookFrom);
                rook.pieceFile = ReturnPiece.PieceFile.valueOf(rookTo.substring(0, 1).toLowerCase());
                rook.pieceRank = Integer.parseInt(rookTo.substring(1));
                board.put(rookTo, rook);
            }
        }
        
        if((piece.pieceType == ReturnPiece.PieceType.WP && from.endsWith("2") && to.endsWith("4")) ||
            (piece.pieceType == ReturnPiece.PieceType.BP && from.endsWith("7") && to.endsWith("5"))) {
            enPassantSquare = String.format("%c%d", to.charAt(0), 
                piece.pieceType == ReturnPiece.PieceType.WP ? 3 : 6);
        } else {
            enPassantSquare = null;
        }
        
        if((piece.pieceType == ReturnPiece.PieceType.WP && to.endsWith("8")) ||
            (piece.pieceType == ReturnPiece.PieceType.BP && to.endsWith("1"))) {
            //try {
            if(promotion == null) {
                promotion = "Q";
            }
                piece.pieceType = ReturnPiece.PieceType.valueOf(
                    (piece.pieceType.name().startsWith("W") ? "W" : "B") + promotion);
            //} catch (IllegalArgumentException e) {
               // piece.pieceType = piece.pieceType.name().startsWith("W") ? 
                   // ReturnPiece.PieceType.WQ : ReturnPiece.PieceType.BQ;
           // }
        }

        // remove the target piece from piecesOnBoard if it exists
        piecesOnBoard.removeIf(p -> 
            p.pieceFile.name().equalsIgnoreCase(to.substring(0, 1)) && 
            p.pieceRank == Integer.parseInt(to.substring(1)));
        
        board.remove(from);
        piece.pieceFile = ReturnPiece.PieceFile.valueOf(to.substring(0, 1).toLowerCase());
        piece.pieceRank = Integer.parseInt(to.substring(1));
        board.put(to, piece);
    }
    
    private static void updateCastlingStatus(ReturnPiece piece, String from) {
        switch(piece.pieceType) {
            case WK:
                whiteKingMoved = true;
                break;
            case BK:
                blackKingMoved = true;
                break;
            case WR:
                if (from.equals("a1")) whiteRooksMoved[0] = true;
                else if (from.equals("h1")) whiteRooksMoved[1] = true;
                break;
            case BR:
                if (from.equals("a8")) blackRooksMoved[0] = true;
                else if (from.equals("h8")) blackRooksMoved[1] = true;
                break;

            default:
              break;
                
        }
    }
    
    private static boolean isCheck(Player player) {
        String kingPos = findKingPosition(player);
        return kingPos != null && isSquareUnderAttack(kingPos, player == Player.white ? Player.black : Player.white, true, false);

        
    }
    
    private static boolean isCheckmate(Player player) {//player is opponent 
        if(!isCheck(player)) {
            return false;
        }
    
        for(ReturnPiece piece : new ArrayList<>(board.values())) {
            if(piece.pieceType.name().startsWith(player == Player.white ? "W" : "B")) {
                String from = piece.pieceFile.name().toLowerCase() + piece.pieceRank;
                //System.out.println(piece.pieceType + " " + from);
                for(int file = 0; file < 8; file++) {
                    for(int rank = 1; rank <= 8; rank++) {
                        String to = String.format("%c%d", 'a' + file, rank);
                        if(isValidMove(piece, from, to, null,player, true,false)) {
                            ReturnPiece originalPiece = board.get(to);
                            board.remove(from);
                            ReturnPiece movedPiece = new ReturnPiece();
                            movedPiece.pieceType = piece.pieceType;
                            movedPiece.pieceFile = ReturnPiece.PieceFile.valueOf(to.substring(0, 1).toLowerCase());
                            movedPiece.pieceRank = Integer.parseInt(to.substring(1));
                            board.put(to, movedPiece);
                            //System.out.println("Trying " + piece.pieceType + " " + from + " to " + to);
    
                            boolean stillInCheck = isCheck(player);
    
                            board.remove(to);
                            board.put(from, piece);
                            if(originalPiece != null) {
                                board.put(to, originalPiece);
                            }
    
                            if(!stillInCheck) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private static boolean isValidCastling(ReturnPiece king, String from, String to) {
        boolean isWhite = king.pieceType == ReturnPiece.PieceType.WK;
        //String kingColor = isWhite ? "white" : "black";
        
        if((isWhite && whiteKingMoved) || (!isWhite && blackKingMoved)) {
            return false;
        }
    
        int fromFile = from.charAt(0) - 'a';
        int fromRank = Integer.parseInt(from.substring(1));
        int toFile = to.charAt(0) - 'a';
        int toRank = Integer.parseInt(to.substring(1));
        int rookFile = toFile > 4 ? 7 : 0;
        String rookPos = String.format("%c%d", 'a' + rookFile, king.pieceRank);
        ReturnPiece rook = board.get(rookPos);
    
        if(rook == null || 
            (isWhite ? (rookFile == 0 ? whiteRooksMoved[0] : whiteRooksMoved[1]) :
                       (rookFile == 0 ? blackRooksMoved[0] : blackRooksMoved[1]))) {
            return false;
        }

        if(fromRank != toRank || Math.abs(fromFile - toFile) != 2) {
            return false;
        }
        int step = rookFile == 7 ? 1 : -1;
        for(int f = king.pieceFile.ordinal() + step; f != rookFile; f += step) {
            String square = String.format("%c%d", 'a' + f, king.pieceRank);
            if(board.containsKey(square)) return false;
        }
        

        for(int f = king.pieceFile.ordinal(); f != toFile + step; f += step) {
            String square = String.format("%c%d", 'a' + f, king.pieceRank);
            System.out.println("Check under attack:" + square);
            if(isSquareUnderAttack(square, isWhite ? Player.black : Player.white,false, true)) {
                return false;
            }
            System.out.println("Not under attack");
        }
        
    //System.out.println("test");
        return true;
    }

    private static boolean isSquareUnderAttack(String square, Player attacker, boolean allowCastling, boolean preCheck) {
        return isSquareUnderAttack(square, attacker, board, allowCastling, preCheck);
    }
    
    private static boolean isSquareUnderAttack(String square, Player attacker, Map<String, ReturnPiece> boardState, boolean allowCastling, boolean preCheck) {
        for(ReturnPiece p : boardState.values()) {
            if((attacker == Player.white && p.pieceType.name().startsWith("W")) ||
                (attacker == Player.black && p.pieceType.name().startsWith("B"))) {
                String from = p.pieceFile.name().toLowerCase() + p.pieceRank;
                if(isValidMove(p, from, square, null, attacker, allowCastling, preCheck)) {
                    // System.out.println("attackerposition: " + p.pieceFile.name().toLowerCase() + p.pieceRank);
                    // System.out.println("from: " + p.pieceFile.name().toLowerCase() + p.pieceRank + " to: " + square + " type: " + p.pieceType);
                    return true;
                }
            }
        }
        return false;
    }
    
    private static String findKingPosition(Player player) {
        String kingType = player == Player.white ? "WK" : "BK";
        for(ReturnPiece p : board.values()) {
            if(p.pieceType.name().equals(kingType)) {
                return p.pieceFile.name().toLowerCase() + p.pieceRank;
            }
        }
        return null;
    }
}
