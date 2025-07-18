# tiny-complier

实现超级 mini 的编译器 | codegen&amp;compiler 生成代码 | 只需要 200 行代码 | 前端编译原理

https://github.com/jamiebuilds/the-super-tiny-compiler


# 词法分析 tokenizer


```js
test('tokenizer', () => {
    const code = `(add 2 (subtract 4 2))`
    const tokens = [
        { type: TokenTypes.Paren, value: '(' },
        { type: TokenTypes.Name, value: 'add' },
        { type: TokenTypes.Number, value: '2' },
        { type: TokenTypes.Paren, value: '(' },
        { type: TokenTypes.Name, value: 'subtract' },
        { type: TokenTypes.Number, value: '4' },
        { type: TokenTypes.Number, value: '2' },
        { type: TokenTypes.Paren, value: ')' },
        { type: TokenTypes.Paren, value: ')' },
    ]
    expect(tokenizer(code)).toEqual(tokens)
})
```

# 语法分析 parser

```js
 it("parser tokens to ast", () => {
    const tokens = [
      { type: TokenTypes.Paren, value: "(" },
      { type: TokenTypes.Name, value: "add" },
      { type: TokenTypes.Number, value: "2" },
      { type: TokenTypes.Paren, value: "(" },
      { type: TokenTypes.Name, value: "subtract" },
      { type: TokenTypes.Number, value: "4" },
      { type: TokenTypes.Number, value: "2" },
      { type: TokenTypes.Paren, value: ")" },
      { type: TokenTypes.Paren, value: ")" },
    ];
    const ast = {
      type: NodeTypes.Program,
      body: [
        {
          type: NodeTypes.CallExpression,
          name: "add",
          params: [
            {
              type: NodeTypes.NumberLiteral,
              value: "2",
            },
            {
              type: NodeTypes.CallExpression,
              name: "subtract",
              params: [
                {
                  type: NodeTypes.NumberLiteral,
                  value: "4",
                },
                {
                  type: NodeTypes.NumberLiteral,
                  value: "2",
                },
              ],
            },
          ],
        },
      ],
    };
    expect(parser(tokens)).toEqual(ast);
  });
```


# traverser 遍历 AST

# transform 转换 AST 

https://astexplorer.net/

![](./ast.png)


父级不是表达式就会加一个`ExpressionStatement`
# codegen&compiler 生成代码

![](./map.png)



## 前端为什么要了解编译原理

很多插件的实现原理都用到了 AST，比如 eslint、webpack、rollup 等等
拿一个大家熟悉的例子来讲就是 vue 的template 是如何编译成 render 函数的，这其中就需要用到编译原理的知识


> 点阵转换

http://www.tianchawang.com/dzz/
https://www.bootschool.net/ascii-art/chinese
https://github.com/cuixiaorui/learn-language
