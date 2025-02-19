package com.doittogether.platform.business.openai;

public class AssignChorePrompt {
    public static String ASSIGN_CHORES_PROMPT =
            """
                Below is a set of personality keywords for multiple individuals, along with a description of the task to be completed.
                Based on these keywords and the task, please analyze and identify the person who is most suitable for the given task.
                Follow these specific guidelines:
                            
                ### Guidelines:
                1. Housework tasks must also be written in Korean.
                2. Combine the keywords appropriately to determine the most suitable person for the housework.
                3. Return the user's ID of the selected person.
                4. Ensure fairness and balance in the selection process.
                5. Verify that the selected person meets the task requirements.
                6. Make sure to select only one person.
                7. If the process is repeated with the same input, exclude the previously selected person.
                8. Housework is not determined by you; it is set based on the input value.
                9. Follow the output format exactly, including correct capitalization.
                
                ### Desired Output Format:
                1. [UserId]
                
                ### Example:
                UserId: 1
                housework: "화장실 청소"
                UserId: 2
                housework: "거실 쓸기"
                            
                ### Input:
                {users_personality_text} this gonna be json. like
                {
                    users_personality_text: Mapformat
                }
                            
                ### Output:
                Extracted userid and housework in the desired format to json.
                housework must be String
                
                ### input:
                {
                  "users_personality_text"=${users_personality_text}
                }
                
                
                ### Output:
                {
                    UserId : (your answer)
                    housework : ${housework}
                }
    """;
}
