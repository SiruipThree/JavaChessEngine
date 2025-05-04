
javac -d ./chess_theirs ./chess_theirs/*.java
javac -d ./chess ./chess/*.java
while true; do
    python3 gen.py > input.txt
    java -cp chess chess.PlayChess < input.txt > output_ours.txt 2>&1
    java -cp chess_theirs chess.PlayChess2 < input.txt > output_theirs.txt 2>&1
    diff output_ours.txt output_theirs.txt >/dev/null
    RES=$?
    grep -o "ILLEGAL_MOVE" output_ours.txt | wc -l
    if [ $RES -eq 0 ]; then
        echo "AC"
    else
        break
    fi
done
